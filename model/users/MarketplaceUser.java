package model.users;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a user in the marketplace system with capabilities for:
 * - User account management (creation, login)
 * - Messaging with other users
 * - Rating system for sellers
 * - Balance management
 * <p>
 * Implements both User and Message interfaces to provide these functionalities.
 * Uses file-based persistence for user data, messages, and ratings.
 *
 * @author Youssef Abdelkader
 * @version April 20 2025
 */
public class MarketplaceUser implements User, Message, Serializable {
    private static final String USERS_FILE = "users.txt";
    private static final Map<String, String> USER_CREDENTIALS = new HashMap<>();
    private static final Object STATIC_LOCK = new Object();
    private final Object lOCK = new Object();
    private final Map<String, ArrayList<Integer>> rATINGS = new HashMap<>();
    private final Object rATINGSLOCK = new Object();
    private static final Map<String, Object> MESSAGE_FILE_LOCKS = new HashMap<>();
    private static final Object MESSAGE_LOCKS_GUARD = new Object();

    private String firstName;
    private String lastName;
    private String password;
    private double balance;
    private String userName;

    static {
        loadUserCredentials();
    }

    /**
     * Creates a new MarketplaceUser with basic information.
     *
     * @param firstName User's first name
     * @param lastName  User's last name
     * @param userName  Unique username
     * @param password  User password
     */
    public MarketplaceUser(String firstName, String lastName, String userName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.balance = 0.0;
        this.userName = userName;
        loadRatingsFromFile();
    }

    /**
     * Creates a new MarketplaceUser with all information including balance.
     *
     * @param firstName  User's first name
     * @param lastName   User's last name
     * @param userName   Unique username
     * @param password   User password
     * @param balance    Initial account balance
     */
    public MarketplaceUser(String firstName, String lastName, String userName,
                           String password, double balance) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.balance = balance;
        this.userName = userName;
        loadRatingsFromFile();
    }

    /**
     * Loads all user credentials from the persistence file.
     * Synchronized to prevent concurrent access issues.
     */
    private static synchronized void loadUserCredentials() {
        synchronized (STATIC_LOCK) {
            try {
                File file = new File(USERS_FILE);
                if (!file.exists()) {
                    if (file.createNewFile()) {
                        System.out.println("User file created: " + file.getName());
                    } else {
                        System.out.println("User file already exists.");
                    }
                }

                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {  // username,password,firstName,lastName,balance
                        USER_CREDENTIALS.put(parts[0], line);
                        // store entire line
                    }
                }
                reader.close();
            } catch (IOException e) {
                System.out.println("Error loading user credentials: " + e.getMessage());
            }
        }
    }

    /**
     * Loads a user from persistent storage.
     *
     * @param inputUserName Username to load
     * @return MarketplaceUser object or null if not found
     */
    public static synchronized MarketplaceUser loadUser(String inputUserName) {
        synchronized (STATIC_LOCK) {
            loadUserCredentials();
            if (!USER_CREDENTIALS.containsKey(inputUserName)) return null;

            String[] parts = USER_CREDENTIALS.get(inputUserName).split(",");
            if (parts.length < 4) return null;
            double balance = parts.length >= 5 ? Double.parseDouble(parts[4]) : 0.0;
            return new MarketplaceUser(parts[2], parts[3], parts[0],
                    parts[1], balance);
        }
    }

    /**
     * Gets or creates a lock object for a specific message file
     *
     * @param username The username whose message file needs locking
     * @return The lock object for that user's message file
     */
    private static Object getMessageFileLock(String username) {
        synchronized (MESSAGE_LOCKS_GUARD) {
            return MESSAGE_FILE_LOCKS.computeIfAbsent(username, k -> new Object());
        }
    }

    /**
     * Sends a message to another user.
     *
     * @param recipientUsername Username of message recipient
     * @param message           Content of the message
     */
    public void sendMessageTo(String recipientUsername, String message) {
        // Get the lock specific to this recipient's message file
        Object messageLock = getMessageFileLock(recipientUsername);

        synchronized (messageLock) {
            String messageFilePath = "messages/" + recipientUsername + ".txt";
            File messageFile = new File(messageFilePath);

            try {
                File dir = new File("messages");
                if (!dir.exists()) {
                    synchronized (MESSAGE_LOCKS_GUARD) {
                        dir.mkdirs();
                    }
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(messageFile, true))) {
                    writer.write("FROM: " + this.userName + "\n");

                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy: h:mma");
                    String formatted = now.format(formatter);
                    writer.write("DATE: " + formatted + "\n");
                    writer.write(message + "\n");
                    writer.write("-------------------\n");
                    System.out.println("Message sent to " + recipientUsername);
                }
            } catch (IOException e) {
                System.out.println("Failed to send message: " + e.getMessage());
            }
        }
    }

    /**
     * Displays all messages received by this user.
     */
    @Override
    public synchronized ArrayList<String> viewMessages() {
        synchronized (lOCK) {
            ArrayList<String> messages = new ArrayList<>();
            String messageFilePath = "messages/" + this.userName + ".txt";
            File messageFile = new File(messageFilePath);

            if (!messageFile.exists()) {
                return messages;
            }

            try {
                BufferedReader reader = new BufferedReader(new FileReader(messageFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    messages.add(line);
                }
                reader.close();
            } catch (IOException e) {
                messages.add("Error reading messages: " + e.getMessage());
            }
            return messages;
        }
    }

    /**
     * Checks if a user exists in the system.
     *
     * @param recipientUsername Username to check
     * @return true if user exists, false otherwise
     */
    public static boolean userExists(String recipientUsername) {

        File file = new File("users.txt");
        if (!file.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].equals(recipientUsername)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error checking user existence: " + e.getMessage());
        }
        return false;
    }

    /**
     * Adds a seller rating for this user.
     *
     * @param rating   Rating value (1-5)
     * @param fromUser User providing the rating
     * @return true if rating was successfully added
     */
    @Override
    public synchronized boolean addSellerRating(int rating, User fromUser) {
        if (rating < 1 || rating > 5) {
            return false;
        }

        synchronized (rATINGSLOCK) {
            // Initialize list if this is the first rating from this user
            rATINGS.computeIfAbsent(fromUser.getUserName(), k -> new ArrayList<>());
            // Add the new rating
            rATINGS.get(fromUser.getUserName()).add(rating);
            saveRatingsToFile();
            return true;
        }
    }


    /**
     * Calculates the average seller rating.
     *
     * @return Average rating (0 if no ratings)
     */
    @Override
    public synchronized double getAverageSellerRating() {
        synchronized (rATINGSLOCK) {
            if (rATINGS.isEmpty()) {
                return 0;
            }

            double sum = 0;
            int count = 0;

            for (ArrayList<Integer> userRatings : rATINGS.values()) {
                for (int rating : userRatings) {
                    sum += rating;
                    count++;
                }
            }

            return count > 0 ? sum / count : 0;
        }
    }


    /**
     * Gets the number of ratings received.
     *
     * @return Count of ratings
     */
    @Override
    public synchronized int getNumberOfRatings() {
        synchronized (rATINGSLOCK) {
            int count = 0;
            for (ArrayList<Integer> userRatings : rATINGS.values()) {
                count += userRatings.size();
            }
            return count;
        }
    }


    /**
     * Saves ratings to persistent storage.
     */
    private void saveRatingsToFile() {
        String ratingsFile = "ratings/" + this.userName + ".txt";
        try {
            File dir = new File("ratings");
            if (!dir.exists()) dir.mkdirs();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ratingsFile))) {
                synchronized (rATINGSLOCK) {
                    for (Map.Entry<String, ArrayList<Integer>> entry : rATINGS.entrySet()) {
                        String rater = entry.getKey();
                        for (Integer rating : entry.getValue()) {
                            writer.write(String.format("%s,%d%n", rater, rating));
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving ratings: " + e.getMessage());
        }
    }


    /**
     * Loads ratings from persistent storage.
     */
    private void loadRatingsFromFile() {
        String ratingsFile = "ratings/" + this.userName + ".txt";
        File file = new File(ratingsFile);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            synchronized (rATINGSLOCK) {
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        String rater = parts[0];
                        int rating = Integer.parseInt(parts[1]);
                        rATINGS.computeIfAbsent(rater, k -> new ArrayList<>());
                        rATINGS.get(rater).add(rating);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading ratings: " + e.getMessage());
        }
    }

    /**
     * Verifies the user's password.
     *
     * @param inputPassword Password to verify
     * @return true if password matches
     */
    public synchronized boolean verifyPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    // Standard getters and setters with synchronization
    @Override
    public synchronized String getFirstName() {
        return firstName;
    }

    @Override
    public synchronized String getLastName() {
        return lastName;
    }

    @Override
    public synchronized String getUserName() {
        return userName;
    }

    @Override
    public synchronized String getPassword() {
        return password;
    }

    @Override
    public synchronized double getBalance() {
        return balance;
    }

    @Override
    public synchronized void setBalance(double balance) {
        this.balance = balance;
    }
}

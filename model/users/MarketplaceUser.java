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
 * 
 * Implements both User and Message interfaces to provide these functionalities.
 * Uses file-based persistence for user data, messages, and ratings.
 *
 * @author Youssef Abdelkader
 * @version April 20 2025
 */
public class MarketplaceUser implements User, Message, Serializable {
    private static final long SERIAL_VERSION_UID = 1L;
    private static final String USERS_FILE = "users.txt";
    private static final Map<String, String> USER_CREDENTIALS = new HashMap<>();
    private static final Object STATIC_LOCK = new Object();
    private final Object LOCK = new Object();
    private final Map<String, Integer> RATINGS = new HashMap<>();
    private final Object RATINGS_LOCK = new Object();
    private static final Map<String, Object> MESSAGE_FILE_LOCKS = new HashMap<>();
    private static final Object MESSAGE_LOCKS_GUARD = new Object();
    
    private String firstName;
    private String lastName;
    private String password;
    private double balance;
    private String userName;

    static {
        LOADUSERCREDENTIALS();
    }

    /**
     * Creates a new MarketplaceUser with basic information.
     * @param firstName User's first name
     * @param lastName User's last name
     * @param userName Unique username
     * @param password User password
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
     * @param firstName User's first name
     * @param lastName User's last name
     * @param userName Unique username
     * @param password User password
     * @param balance Initial account balance
     * @param saveToFile Whether to save to persistence immediately
     */
    public MarketplaceUser(String firstName, String lastName, String userName,
                         String password, double balance, boolean saveToFile) {
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
    private static synchronized void LOADUSERCREDENTIALS() {
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
                        // Store entire line containing user data
                        USER_CREDENTIALS.put(parts[0], line);
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
     * @param inputUserName Username to load
     * @return MarketplaceUser object or null if not found
     */
    public static synchronized MarketplaceUser loadUser(String inputUserName) {
        synchronized (STATIC_LOCK) {
            LOADUSERCREDENTIALS();
            if (!USER_CREDENTIALS.containsKey(inputUserName)) return null;

            String[] parts = USER_CREDENTIALS.get(inputUserName).split(",");
            if (parts.length < 4) return null;
            double balance = parts.length >= 5 ? Double.parseDouble(parts[4]) : 0.0;
            return new MarketplaceUser(parts[2], parts[3], parts[0],
                parts[1], balance, false);
        }
    }

    /**
     * Verifies user credentials against stored values.
     * @param inputUserName Username to verify
     * @param inputPassword Password to verify
     * @return true if credentials match, false otherwise
     */
    public static synchronized boolean verifyCredentials(String inputUserName, String inputPassword) {
        synchronized (STATIC_LOCK) {
            LOADUSERCREDENTIALS(); // Reload credentials to get latest data
            return USER_CREDENTIALS.containsKey(inputUserName) &&
                   USER_CREDENTIALS.get(inputUserName).split(",")[1].equals(inputPassword);
        }
    }

    /**
     * Saves user data to the persistence file.
     * @param userData Formatted user data string
     */
    private synchronized void saveToFile(String userData) {
        synchronized (LOCK) {
            try {
                FileWriter fw = new FileWriter(USERS_FILE, true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(userData + "\n");
                bw.close();
            } catch (IOException e) {
                System.out.println("Error saving to file: " + e.getMessage());
            }
        }
    }

    /**
     * Gets or creates a lock object for a specific message file
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
     * @param recipientUsername Username of message recipient
     * @param message Content of the message
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
        synchronized (LOCK) {
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
     * @param rating Rating value (1-5)
     * @param fromUser User providing the rating
     * @return true if rating was successfully added
     */
    @Override
    public synchronized boolean addSellerRating(int rating, User fromUser) {
        if (rating < 1 || rating > 5) {
            return false;
        }
        
        synchronized (RATINGS_LOCK) {
            RATINGS.put(fromUser.getUserName(), rating);
            saveRatingsToFile();
            return true;
        }
    }

    /**
     * Calculates the average seller rating.
     * @return Average rating (0 if no ratings)
     */
    @Override
    public synchronized double getAverageSellerRating() {
        synchronized (RATINGS_LOCK) {
            if (RATINGS.isEmpty()) {
                return 0;
            }
            double sum = 0;
            for (int rating : RATINGS.values()) {
                sum += rating;
            }
            return sum / RATINGS.size();
        }
    }

    /**
     * Gets the number of ratings received.
     * @return Count of ratings
     */
    @Override
    public synchronized int getNumberOfRatings() {
        synchronized (RATINGS_LOCK) {
            return RATINGS.size();
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
                synchronized (RATINGS_LOCK) {
                    for (Map.Entry<String, Integer> entry : RATINGS.entrySet()) {
                        writer.write(String.format("%s,%d%n", entry.getKey(), entry.getValue()));
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
            synchronized (RATINGS_LOCK) {
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        RATINGS.put(parts[0], Integer.parseInt(parts[1]));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading ratings: " + e.getMessage());
        }
    }

    /**
     * Creates a new user account.
     * @param newFirstName User's first name
     * @param newLastName User's last name
     * @param newUserName Unique username
     * @param newPassword User password
     * @return true if creation was successful
     */
    @Override
    public synchronized boolean createNewUser
    (String newFirstName, String newLastName, 
            String newUserName, String newPassword) {
        synchronized (STATIC_LOCK) {
            if (newFirstName.isEmpty() || newLastName.isEmpty() || 
                newUserName.isEmpty() || newPassword.isEmpty()) {
                System.out.println("All fields must be filled.");
                return false;
            }
            if (USER_CREDENTIALS.containsKey(newUserName)) {
                System.out.println("Username already exists.");
                return false;
            }

            String userData = String.format(
                "%s,%s,%s,%s,%.2f", 
                newUserName, 
                newPassword, 
                newFirstName, 
                newLastName, 
                balance);
            saveToFile(userData);

            USER_CREDENTIALS.put(newUserName, newPassword);
            return true;
        }
    }

    /**
     * Authenticates a user.
     * @param inputUserName Username to authenticate
     * @param inputPassword Password to verify
     * @return true if authentication succeeds
     */
    @Override
    public synchronized boolean login(String inputUserName, String inputPassword) {
        return verifyCredentials(inputUserName, inputPassword);
    }

    /**
     * Verifies the user's password.
     * @param inputPassword Password to verify
     * @return true if password matches
     */
    public synchronized boolean verifyPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    // Standard getters and setters with synchronization
    @Override public synchronized String getFirstName() { return firstName; }
    @Override public synchronized String getLastName() { return lastName; }
    @Override public synchronized String getUserName() { return userName; }
    @Override public synchronized String getPassword() { return password; }
    @Override public synchronized double getBalance() { return balance; }
    @Override public synchronized void setFirstName(String firstName) { this.firstName = firstName; }
    @Override public synchronized void setLastName(String lastName) { this.lastName = lastName; }
    @Override public synchronized void setUsername(String newUserName) { this.userName = newUserName; }
    @Override public synchronized void setPassword(String password) { this.password = password; }
    @Override public synchronized void setBalance(double balance) { this.balance = balance; }

    /**
     * Registers a new user with the system.
     * @param newFirstName User's first name
     * @param newLastName User's last name
     * @param newUserName Unique username
     * @param newPassword User password
     * @return New MarketplaceUser or null if registration failed
     */
    public static MarketplaceUser registerNewUser(String newFirstName, String newLastName, 
            String newUserName, String newPassword) {
        if (USER_CREDENTIALS.containsKey(newUserName)) {
            System.out.println("Username already exists.");
            return null;
        }
        MarketplaceUser user = new MarketplaceUser(
            newFirstName, 
            newLastName, 
            newUserName, 
            newPassword, 
            0.0, 
            false);
        user.createNewUser(newFirstName, newLastName, newUserName, newPassword);
        return user;
    }
}

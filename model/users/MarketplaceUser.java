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
 */
public class MarketplaceUser implements User, Message, Serializable {
    private static final long serialVersionUID = 1L;
    private static final String USERS_FILE = "users.txt";
    private static final Map<String, String> userCredentials = new HashMap<>();
    private static final Object staticLock = new Object();
    private final Object lock = new Object();
    private final Map<String, Integer> ratings = new HashMap<>();
    private final Object ratingsLock = new Object();
    private static final Map<String, Object> messageFileLocks = new HashMap<>();
    private static final Object messageLocksGuard = new Object();
    
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
    private static synchronized void loadUserCredentials() {
        synchronized(staticLock) {
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
                        userCredentials.put(parts[0], line); // store entire line: username,password,firstName,lastName,balance
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
     * @param userName Username to load
     * @return MarketplaceUser object or null if not found
     */
    public static synchronized MarketplaceUser loadUser(String userName) {
        synchronized(staticLock) {
            loadUserCredentials();
            if (!userCredentials.containsKey(userName)) return null;

            String[] parts = userCredentials.get(userName).split(",");
            if (parts.length < 4) return null;
            double balance = parts.length >= 5 ? Double.parseDouble(parts[4]) : 0.0;
            return new MarketplaceUser(parts[2], parts[3], parts[0], parts[1], balance, false);
        }
    }

    /**
     * Verifies user credentials against stored values.
     * @param userName Username to verify
     * @param password Password to verify
     * @return true if credentials match, false otherwise
     */
    public static synchronized boolean verifyCredentials(String userName, String password) {
        synchronized(staticLock) {
            loadUserCredentials(); // Reload credentials to get latest data
            return userCredentials.containsKey(userName) &&
                   userCredentials.get(userName).split(",")[1].equals(password);
        }
    }

    /**
     * Saves user data to the persistence file.
     * @param userData Formatted user data string
     */
    private synchronized void saveToFile(String userData) {
        synchronized(lock) {
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
        synchronized(messageLocksGuard) {
            return messageFileLocks.computeIfAbsent(username, k -> new Object());
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
        
        synchronized(messageLock) {
            String messageFilePath = "messages/" + recipientUsername + ".txt";
            File messageFile = new File(messageFilePath);

            try {
                File dir = new File("messages");
                if (!dir.exists()) {
                    synchronized(messageLocksGuard) {
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
    public synchronized void viewMessages() {
    @Override
    public synchronized ArrayList<String> viewMessages() {
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
        
        synchronized(ratingsLock) {
            ratings.put(fromUser.getUserName(), rating);
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
        synchronized(ratingsLock) {
            if (ratings.isEmpty()) {
                return 0;
            }
            double sum = 0;
            for (int rating : ratings.values()) {
                sum += rating;
            }
            return sum / ratings.size();
        }
    }

    /**
     * Gets the number of ratings received.
     * @return Count of ratings
     */
    @Override
    public synchronized int getNumberOfRatings() {
        synchronized(ratingsLock) {
            return ratings.size();
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
                synchronized(ratingsLock) {
                    for (Map.Entry<String, Integer> entry : ratings.entrySet()) {
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
            synchronized(ratingsLock) {
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        ratings.put(parts[0], Integer.parseInt(parts[1]));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading ratings: " + e.getMessage());
        }
    }

    /**
     * Creates a new user account.
     * @param firstName User's first name
     * @param lastName User's last name
     * @param userName Unique username
     * @param password User password
     * @return true if creation was successful
     */
    @Override
    public synchronized boolean createNewUser(String firstName, String lastName, String userName, String password) {
        synchronized(staticLock) {
            if (firstName.isEmpty() || lastName.isEmpty() || userName.isEmpty() || password.isEmpty()) {
                System.out.println("All fields must be filled.");
                return false;
            }
            if (userCredentials.containsKey(userName)) {
                System.out.println("Username already exists.");
                return false;
            }

            String userData = String.format("%s,%s,%s,%s,%.2f", userName, password, firstName, lastName, balance);
            saveToFile(userData);

            userCredentials.put(userName, password);
            return true;
        }
    }

    /**
     * Authenticates a user.
     * @param userName Username to authenticate
     * @param password Password to verify
     * @return true if authentication succeeds
     */
    @Override
    public synchronized boolean login(String userName, String password) {
        return verifyCredentials(userName, password);
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
    @Override public synchronized void setUsername(String userName) { this.userName = userName; }
    @Override public synchronized void setPassword(String password) { this.password = password; }
    @Override public synchronized void setBalance(double balance) { this.balance = balance; }

    /**
     * Registers a new user with the system.
     * @param firstName User's first name
     * @param lastName User's last name
     * @param userName Unique username
     * @param password User password
     * @return New MarketplaceUser or null if registration failed
     */
    public static MarketplaceUser registerNewUser(String firstName, String lastName, String userName, String password) {
        if (userCredentials.containsKey(userName)) {
            System.out.println("Username already exists.");
            return null;
        }
        MarketplaceUser user = new MarketplaceUser(firstName, lastName, userName, password, 0.0, false);
        user.createNewUser(firstName, lastName, userName, password);
        return user;
    }
}

package model.users;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MarketplaceUser implements User, Serializable, Message {
    private static final long serialVersionUID = 1L;
    private static final String USERS_FILE = "users.txt";
    private static final Map<String, String> userCredentials = new HashMap<>();
    private static final Object staticLock = new Object();
    private final Object lock = new Object();
    
    private String firstName;
    private String lastName;
    private String password;
    private double balance;
    private String userName;

    static {
        loadUserCredentials();
    }

    public MarketplaceUser(String firstName, String lastName, String userName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.balance = 0.0;
        this.userName = userName;
    }

    public MarketplaceUser(String firstName, String lastName, String userName,
                           String password, double balance, boolean saveToFile) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.balance = balance;
        this.userName = userName;
    }

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

    public static synchronized MarketplaceUser loadUser(String userName) {
        synchronized(staticLock) {
            loadUserCredentials();
            if (!userCredentials.containsKey(userName)) return null;

            String[] parts = userCredentials.get(userName).split(",");
            if (parts.length < 4) return null;
            double balance = parts.length >= 5 ? Double.parseDouble(parts[4]) : 0.0;
            return new MarketplaceUser(parts[2], parts[3], parts[0], parts[1], balance, false); // use the private constructor
        }
    }

    public static synchronized boolean verifyCredentials(String userName, String password) {
        synchronized(staticLock) {
            loadUserCredentials(); // Reload credentials to get latest data
            return userCredentials.containsKey(userName) &&
                   userCredentials.get(userName).split(",")[1].equals(password);
        }
    }

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

    public synchronized void sendMessageTo(String recipientUsername, String message) {
        String messageFilePath = "messages/" + recipientUsername + ".txt";
        File messageFile = new File(messageFilePath);

        try {
            File dir = new File("messages");
            if (!dir.exists()) dir.mkdirs();

            BufferedWriter writer = new BufferedWriter(new FileWriter(messageFile, true));
            writer.write("FROM: " + this.userName + "\n");

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy: h:mma"); // Format time to be prettier
            String formatted = now.format(formatter);
            writer.write("DATE: " + formatted + "\n");
            writer.write(message + "\n");
            writer.write("-------------------\n");
            writer.close();
            System.out.println("Message sent to " + recipientUsername);
        } catch (IOException e) {
            System.out.println("Failed to send message: " + e.getMessage());
        }
    }
    
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

        return messages;
    }
 

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

    @Override
    public synchronized boolean login(String userName, String password) {
        return verifyCredentials(userName, password);
    }

    public synchronized boolean verifyPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

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
    public synchronized void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public synchronized void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public synchronized void setUsername(String userName) {
        this.userName = userName;
    }

    @Override
    public synchronized void setPassword(String password) {
        this.password = password;
    }

    @Override
    public synchronized void setBalance(double balance) {
        this.balance = balance;
    }

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

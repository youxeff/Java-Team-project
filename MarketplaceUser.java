import java.util.HashMap;
import java.util.Map;
import java.io.*;

public class MarketplaceUser implements User {
    private static final String USERS_FILE = "users.txt";
    private static final Map<String, String> userCredentials = new HashMap<>();
    
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
        createNewUser(firstName, lastName, userName, password);
    }

    private static void loadUserCredentials() {
        try {
            File file = new File(USERS_FILE);
            if (!file.exists()) {
                file.createNewFile();
                return;
            }
            
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {  // username,password,firstName,lastName
                    userCredentials.put(parts[0], parts[1]);
                    // Store user details for later use
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading user credentials: " + e.getMessage());
        }
    }

    public static boolean verifyCredentials(String userName, String password) {
        loadUserCredentials(); // Reload credentials to get latest data
        return userCredentials.containsKey(userName) && 
               userCredentials.get(userName).equals(password);
    }

    private void saveToFile(String userData) {
        try {
            FileWriter fw = new FileWriter(USERS_FILE, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(userData + "\n");
            bw.close();
        } catch (IOException e) {
            System.out.println("Error saving to file: " + e.getMessage());
        }
    }

    @Override
    public boolean createNewUser(String firstName, String lastName, String userName, String password) {
        if (firstName.isEmpty() || lastName.isEmpty() || userName.isEmpty() || password.isEmpty()) {
            System.out.println("All fields must be filled.");
            return false;
        }
        if (userCredentials.containsKey(userName)) {
            System.out.println("Username already exists.");
            return false;
        }

        String userData = String.format("%s,%s,%s,%s", userName, password, firstName, lastName);
        saveToFile(userData);
        
        userCredentials.put(userName, password);
        return true;
    }

    @Override
    public boolean login(String userName, String password) {
        return verifyCredentials(userName, password);
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public void setUsername(String userName) {
        this.userName = userName;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void setBalance(double balance) {
        this.balance = balance;
    }
}

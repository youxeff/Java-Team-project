import java.util.HashMap;
import java.util.Map;
import java.io.*;

public abstract class AbstractUser implements User {
    protected static final Map<String, String> userCredentials = new HashMap<>();
    private static final String USERS_FILE = "users.txt";
    private static final String SELLERS_FILE = "sellers.txt";
    private static final String BUYERS_FILE = "buyers.txt";

    static {
        loadUserCredentials();
    }

    AbstractUser(String firstName, String lastName, String userName, String password) {
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
                if (parts.length >= 2) {
                    userCredentials.put(parts[0], parts[1]);
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading user credentials: " + e.getMessage());
        }
    }

    private void saveToFile(String fileName, String userData) {
        try {
            FileWriter fw = new FileWriter(fileName, true);
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

        // Save to users.txt
        String userData = String.format("%s,%s,%s,%s,%s", userName, password, firstName, lastName, this instanceof Seller ? "SELLER" : "BUYER");
        saveToFile(USERS_FILE, userData);

        // Save to respective role file
        String roleData = String.format("%s,%s,%s,%s,0.0", userName, firstName, lastName, password);
        saveToFile(this instanceof Seller ? SELLERS_FILE : BUYERS_FILE, roleData);

        userCredentials.put(userName, password);
        return true;
    }

    @Override
    public boolean login(String userName, String password) {
        return userCredentials.containsKey(userName) && userCredentials.get(userName).equals(password);
    }


}
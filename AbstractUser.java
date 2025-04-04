import java.util.HashMap;
import java.util.Map;
import java.io.*;

public abstract class AbstractUser implements User {
    private static final String USERS_FILE = "users.txt";
    protected static final Map<String, UserData> userCredentials = new HashMap<>();
    
    protected static class UserData {
        String password;
        Role role;
        
        UserData(String password, Role role) {
            this.password = password;
            this.role = role;
        }
    }
    
    static {
        loadUserCredentials();
    }
    
    private Role role;
    
    AbstractUser(String firstName, String lastName, String userName, String password, Role role) {
        this.role = role;
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
                if (parts.length >= 3) {
                    userCredentials.put(parts[0], new UserData(parts[1], Role.valueOf(parts[4])));
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading user credentials: " + e.getMessage());
        }
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

        String userData = String.format("%s,%s,%s,%s,%s", userName, password, firstName, lastName, role);
        saveToFile(userData);
        
        userCredentials.put(userName, new UserData(password, role));
        return true;
    }

    @Override
    public boolean login(String userName, String password) {
        UserData userData = userCredentials.get(userName);
        return userData != null && userData.password.equals(password);
    }
    
    @Override
    public Role getRole() {
        return role;
    }
    
    @Override
    public void setRole(Role role) {
        this.role = role;
    }
    
    @Override
    public boolean addRole(Role newRole) {
        if (role == Role.BOTH || newRole == this.role) {
            return false;
        }
        role = Role.BOTH;
        return true;
    }
}
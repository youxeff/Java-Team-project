import java.util.HashMap;
import java.util.Map;

public abstract class AbstractUser implements User {
    protected static final Map<String, String> userCredentials = new HashMap<>();


    AbstractUser(String firstName, String lastName, String userName, String password) {
        createNewUser(firstName, lastName, userName, password);

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
        userCredentials.put(userName, password);
        return true;
    }

    @Override
    public boolean login(String userName, String password) {
        return userCredentials.containsKey(userName) && userCredentials.get(userName).equals(password);
    }

    // getters/setters can be implemented here or in child classes
}
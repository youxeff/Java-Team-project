package model.users;
public interface User {
    String getFirstName();
    String getLastName();
    String getUserName();
    String getPassword();
    double getBalance();

    void setFirstName(String firstName);
    void setLastName(String lastName);
    void setUsername(String userName);
    void setPassword(String password);
    void setBalance(double balance);

    void loadUserCredentials();
    boolean verifyCredentials(String userName, String password);
    void saveToFile(String userData);
    MarketplaceUser loadUser(String userName);
    boolean userExists(String recipientUsername);
    boolean createNewUser(String firstName, String lastName, String userName, String password);
    
    

    boolean createNewUser(String firstName, String lastName, String userName, String password);
    boolean login(String userName, String password);
    boolean verifyPassword(String inputPassword);
    MarketplaceUser registerNewUser(String firstName, String lastName, String userName, String password);
}


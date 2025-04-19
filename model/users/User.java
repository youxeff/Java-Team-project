package model.users;

/**
 * Defines core user functionality for the marketplace system.
 * Includes account management, authentication, and seller rating capabilities.
 */
public interface User {
    // Basic user information accessors
    String getFirstName();
    String getLastName();
    String getUserName();
    String getPassword();
    double getBalance();

    // Basic user information mutators
    void setFirstName(String firstName);
    void setLastName(String lastName);
    void setUsername(String userName);
    void setPassword(String password);
    void setBalance(double balance);

    // Account management
    boolean createNewUser(String firstName, String lastName, String userName, String password);
    boolean login(String userName, String password);
    boolean verifyPassword(String inputPassword);

    /**
     * Adds a rating for this user as a seller.
     * @param rating Rating value between 1-5
     * @param fromUser The user providing the rating
     * @return true if rating was successfully added
     */
    boolean addSellerRating(int rating, User fromUser);
    
    /**
     * Gets the average seller rating for this user.
     * @return average rating between 1-5, or 0 if no ratings
     */
    double getAverageSellerRating();
    
    /**
     * Gets the total number of ratings received as a seller.
     * @return number of ratings received
     */
    int getNumberOfRatings();
}

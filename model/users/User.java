package model.users;

/**
 * Defines core user functionality for the marketplace system.
 * Includes account management, authentication, and seller rating capabilities.
 * 
 * @author Youssef Abdelkader
 * @author Anthony Kim  
 * @author Caroline Murphy
 * @author Eric Yen
 * @author Isaac Yoon
 * @version April 20 2025
 */
public interface User {
    /**
     * Gets the user's first name.
     * @return The user's first name
     */
    String getFirstName();

    /**
     * Gets the user's last name.
     * @return The user's last name
     */
    String getLastName();

    /**
     * Gets the user's username.
     * @return The user's username
     */
    String getUserName();

    /**
     * Gets the user's password.
     * @return The user's password
     */
    String getPassword();

    /**
     * Gets the user's current balance.
     * @return The user's balance
     */
    double getBalance();

    /**
     * Sets the user's balance.
     * @param balance The new balance
     */
    void setBalance(double balance);

    /**
     * Verifies if a given password matches the user's password.
     * @param inputPassword Password to verify
     * @return true if password matches
     */
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

    /**
     * Sends a message to another user.
     *
     * @param recipientUsername the recipient's username
     * @param message the message content
     */
    void sendMessageTo(String recipientUsername, String message);
}

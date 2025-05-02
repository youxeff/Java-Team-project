package Network;

import java.io.IOException;
import model.users.User;

/**
 * Interface defining the client handler functionality for the marketplace system.
 * Handles individual client connections and their interactions with the marketplace.
 * 
 * @author Youssef Abdelkader
 * @version April 20 2025
 */
public interface IClientHandler {
    
    /**
     * Handles new user registration process over the network connection.
     * @throws IOException if there's an error in network communication
     */
    void registerUser() throws IOException;

    /**
     * Manages user authentication and login over the network connection.
     * @throws IOException if there's an error in network communication
     */
    void loginUser() throws IOException;

    /**
     * Displays and manages the logged-in user's menu options over the network.
     * @throws IOException if there's an error in network communication
     */
    void showUserMenu() throws IOException;

    /**
     * Shows the current user's profile information over the network.
     */
    void displayUserProfile();

    /**
     * Provides options for buying or selling items over the network.
     * @throws IOException if there's an error in network communication
     */
    void buyOrSell() throws IOException;

    /**
     * Handles the item selling process over the network connection.
     * @throws IOException if there's an error in network communication
     */
    void sellItem() throws IOException;

    /**
     * Handles the item purchasing process over the network connection.
     * @throws IOException if there's an error in network communication
     */
    void buyItem() throws IOException;

    /**
     * Displays all messages received by the current user.
     */
    void viewMessages();

    /**
     * Prompts for and handles seller rating after a purchase.
     * @param seller the User object representing the seller to be rated
     * @throws IOException if there's an error in network communication
     */
    void promptForSellerRating(User seller) throws IOException;
}

package Network;

import java.io.IOException;
import model.users.User;

/**
 * Interface defining the client handler functionality for the marketplace system.
 * Handles individual client connections and their interactions with the marketplace.
 * 
 * @author Youssef Abdelkader
 * @author Anthony Kim
 * @author Isaac Yoon
 * @author Caroline Murphy
 * @author Eric Yen
 * @version May 4, 2025
 */
public interface IClientHandler {
    /**
     * Handles the item selling process over the network connection.
     * @throws IOException if there's an error in network communication
     */
    void sellItem() throws IOException;

    /**
     * Prompts for and handles seller rating after a purchase.
     * @param seller the User object representing the seller to be rated
     * @throws IOException if there's an error in network communication
     */
    void promptForSellerRating(User seller) throws IOException;

    /**
     * Displays the authentication GUI for login or registration.
     */
    void createAuthGUI();
}

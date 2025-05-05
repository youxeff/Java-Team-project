package model.items;
import java.io.IOException;
import java.util.ArrayList;
import model.users.User;

/**
 * Represents a marketplace that manages items for sale, sellers, buyers, and user account persistence.
 * Handles transactions, searches, and user data file management.
 *
 * @author Youssef Abdelkader
 * @author Anthony Kim  
 * @author Caroline Murphy
 * @author Eric Yen
 * @author Isaac Yoon
 * @version April 20 2025
 */
public interface IMarketplace {

    /**
     * Searches for sellers by name (case-insensitive partial match).
     * Matches against username, first name, or last name.
     *
     * @param sellerSearch the name or partial name to search for
     * @return new ArrayList of matching Sellers (never null)
     * @throws IllegalArgumentException if sellerSearch is null
     */
    ArrayList<User> searchSeller(String sellerSearch);

    /**
     * Searches for items by name (case-insensitive partial match).
     * Searches through all available items in the marketplace.
     *
     * @param nameSearch the name or partial name to search for
     * @return new ArrayList of matching Items (never null)
     * @throws IllegalArgumentException if nameSearch is null
     */
    ArrayList<Item> searchByName(String nameSearch);

    /**
     * Searches for items by category (case-insensitive exact match).
     * Categories include: Apparel, Collectible, Electronic, Home, Vehicle.
     *
     * @param categorySearch the category to search for
     * @return new ArrayList of matching Items (never null)
     * @throws IllegalArgumentException if categorySearch is null
     */
    ArrayList<Item> searchByCategory(String categorySearch);

    /**
     * Updates the user data file with new account information.
     * Called when new user account is created or user data is modified.
     *
     * @param user the User object to add to or update in the file
     * @return true if operation succeeded, false otherwise
     * @throws IOException if file I/O operations fail
     */
    boolean updateUserData(User user) throws IOException;

    /**
     * Creates the user data file if it doesn't exist yet.
     * Called anytime program runs to prevent errors.
     *
     * @return true if file was created or already exists, false if creation failed
     * @throws IOException if file creation fails
     */
    boolean initializeUserDataFile() throws IOException;

    /**
     * Loads all users from the info file into memory.
     * Called when verifying user information or loading marketplace state.
     *
     * @return ArrayList of all registered Users
     * @throws IOException if file reading fails
     */
    ArrayList<User> loadAllUsers() throws IOException;

    /**
     * Adds an item to the marketplace.
     *
     * @param item the Item object to add
     */
    void addItem(Item item);

    /**
     * Authenticates a user based on username and password.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return the authenticated User object, or null if authentication fails
     */
    User authenticateUser(String username, String password);

    /**
     * Retrieves all available items in the marketplace.
     *
     * @return ArrayList of all available Items
     */
    ArrayList<Item> getAvailableItems();

    /**
     * Processes the purchase of an item by a buyer.
     *
     * @param item the Item object to purchase
     * @param buyer the User object representing the buyer
     * @return true if the purchase was successful, false otherwise
     */
    boolean purchaseItem(Item item, User buyer);

    /**
     * Loads all transactions for a specific user.
     *
     * @param username the username of the user
     * @return ArrayList of transactions, where each transaction is represented as a String array
     */
    ArrayList<String[]> loadTransactions(String username);
}

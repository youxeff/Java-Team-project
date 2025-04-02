import java.util.ArrayList;
import java.io.IOException;

/**
 * Represents a marketplace that manages items for sale, sellers, buyers, and user account persistence.
 * Handles transactions, searches, and user data file management.
 */
public interface IMarketplace {

    // Search Methods

    /**
     * Searches for sellers by name (case-insensitive partial match)
     *
     * @param sellerSearch the name or partial name to search for
     * @return new ArrayList of matching Sellers (never null)
     * @throws IllegalArgumentException if sellerSearch is null
     */
    ArrayList<Seller> searchSeller(String sellerSearch);

    /**
     * Searches for items by name (case-insensitive partial match)
     *
     * @param nameSearch the name or partial name to search for
     * @return new ArrayList of matching Items (never null)
     * @throws IllegalArgumentException if nameSearch is null
     */
    ArrayList<Item> searchByName(String nameSearch);

    /**
     * Searches for items by category (case-insensitive exact match)
     *
     * @param categorySearch the category to search for
     * @return new ArrayList of matching Items (never null)
     * @throws IllegalArgumentException if categorySearch is null
     */
    ArrayList<Item> searchByCategory(String categorySearch);

    /**
     * Updates the user data file with new account information
     * Called when new user account is created (constructors for Buyer/Seller)
     *
     * @param user the User object to add to the file
     * @return true if operation succeeded, false otherwise
     * @throws IOException if file I/O operations fail
     */
    boolean updateUserData(User user) throws IOException;

    /**
     * Creates the user data file if it doesn't exist yet
     * Called anytime program runs to prevent errors
     *
     * @return true if file was created or already exists, false if creation failed
     * @throws IOException if file creation fails somehow
     */
    boolean initializeUserDataFile() throws IOException;

    /**
     * Loads all users from the info file
     * Called when verifying user information
     *
     * @return ArrayList of all registered Users
     * @throws IOException if file reading fails somehow
     */
    ArrayList<User> loadAllUsers() throws IOException;
}

package model.items;
import java.util.ArrayList;
import model.users.User;

/**
 * Interface defining the common functionality for all items in the marketplace.
 */
public interface Item {
    /**
     * Attempts to sell the item to a user
     * @param buyer The user attempting to buy the item
     * @return true if the sale was successful, false otherwise
     */
    Boolean sellItem(User buyer);

    /**
     * Marks the item as deleted (unavailable)
     * @return true if the operation was successful
     */
    Boolean deleteItem();
    
    /**
     * Marks the item as sold
     */
    void markSold();

    /**
     * Searches for items by name (to be implemented in Marketplace)
     * @param name The name to search for
     * @return List of matching items
     */
    ArrayList<Item> searchByName(String name);
    
    /**
     * Searches for items by category (to be implemented in Marketplace)
     * @param category The category to search for
     * @return List of matching items
     */
    ArrayList<Item> searchByCategory(String category);

    /**
     * Returns a string representation of the item
     * @return String representation
     */
    String toString();

    // Getter methods
    String getName();
    double getCost();
    User getSoldBy();
    boolean isAvailable();
    String getImage();
    String getCategory();
}

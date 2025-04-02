/**
 * This interface represents an item. It will be implemented in classes that will represent items of a specific category.
 * These classes will have the following fields:
 * name: String, name of the item
 * cost: double, cost of the item
 * soldBy: Seller, seller selling the item
 * isAvailable: boolean, whether the item is available to buy (true if available)
 * image: String, path to image of the item
 */
interface Item {
    /**
     * Processes the sale of the item to a buyer. Returns true if successful and false if not.
     *
     * @param buyer, The buyer of the item.
     * @return Boolean, true if sale goes through
     */
    Boolean sellItem(Buyer buyer);

    /**
     * Removes the item listing. Returns true if successful and false if not.
     *
     * @return Boolean, true if deletion goes through
     */
    Boolean deleteItem();

    /**
     * Search items by name
     *
     * @param name, The name of the item.
     * @return List<Item>, list of matches
     */
    List<Item> searchByName(String name);

    /**
     * Search items by category
     *
     * @param category, The category of the item.
     * @return List<Item>, list of matches
     */
    List<Item> searchByCategory(String category);

    /**
     * Mark item as sold (change isAvailable to false)
     */
    void markSold();

    /**
     * Returns a formatted string with all the details (name, cost, seller, etc.). Overrides Object superclass’s toString method.
     *
     * @return String, string representation of the item
     */
    String toString();
}
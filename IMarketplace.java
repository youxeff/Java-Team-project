/**
 * Represents a marketplace that manages items for sale, sellers, and buyers.
 * Handles transactions and searches.
 */
interface IMarketplace {
    /**
     * Searches for sellers by name
     *
     * @param sellerSearch: the name or partial name to search for
     * @return new ArrayList of matching Sellers. If empty, no matches.
     * @throws IllegalArgumentException if sellerSearch is null
     */
    ArrayList<Seller> searchSeller(String sellerSearch);

    /**
     * Searches for items by name
     *
     * @param itemSearch: the name or partial name to search for
     * @return new ArrayList of matching items. If empty, no matches.
     * @throws IllegalArgumentException if nameSearch is null
     */
    ArrayList<Item> searchByName(String nameSearch);

    /**
     * Searches for items by category
     *
     * @param sellerSearch: the name or partial name to search for
     * @return new ArrayList of matching Sellers (never null). If empty, no matches.
     * @throws IllegalArgumentException if categorySearch is null
     */
    ArrayList<Item> searchByCategory(String categorySearch);


}
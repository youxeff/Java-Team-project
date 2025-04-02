/**
 * This interface represents an apparel item.
 * The fields of the Apparel class that will extend IApparel will be the same as listed in the Item interface, but
 * will also include fields like:
 *  size: String, size of the item ("small", "medium", "8", etc.)
 *  type: String, type of item ("Shirt", "Shoes", etc.)
 */
public interface IApparel extends Item{
    // inherits all methods from Item
    // will include getters and setters for additional fields as listed above
}
/**
 * This interface represents a home item.
 * The fields of the Home class that will extend IHome will be the same as listed in the Item interface, but
 * will also include fields like:
 *  type: String, type of item ("Comic Book", "Figurine", "Card Game", etc.)
 */
public interface ICollectibles extends Item{
    // inherits all methods from Item
    // will include getters and setters for additional fields as listed above
}
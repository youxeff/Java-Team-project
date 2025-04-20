package model.items;

/**
 * Defines the interface for home items in the marketplace.
 * Specifies home-specific properties and operations.
 */
public interface IHome {
    /**
     * @return The type of home item
     */
    String getType();
    
    /**
     * Sets the type of home item.
     * @param type New type value
     */
    void setType(String type);
}

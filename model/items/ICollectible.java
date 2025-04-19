package model.items;

/**
 * Defines the interface for collectible items in the marketplace.
 * Specifies collectible-specific properties and operations.
 */
public interface ICollectible {
    /**
     * @return The type of collectible
     */
    String getType();
    
    /**
     * @return The condition of the collectible
     */
    String getCondition();
    
    /**
     * Sets the type of collectible.
     * @param type New type value
     */
    void setType(String type);
    
    /**
     * Sets the condition of the collectible.
     * @param condition New condition value
     */
    void setCondition(String condition);
}

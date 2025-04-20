package model.items;

/**
 * Defines the interface for collectible items in the marketplace.
 * Specifies collectible-specific properties and operations.
 * 
 * @author Youssef Abdelkader
 * @author Anthony Kim  
 * @author Caroline Murphy
 * @author Eric Yen
 * @author Isaac Yoon
 * @version April 20 2025
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

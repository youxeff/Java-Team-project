package model.items;

/**
 * Defines the interface for apparel items in the marketplace.
 * Specifies apparel-specific properties and operations.
 */
public interface IApparel {
    /**
     * @return The size of the apparel item
     */
    String getSize();
    
    /**
     * @return The color of the apparel item
     */
    String getColor();
    
    /**
     * @return The brand of the apparel item
     */
    String getBrand();
    
    /**
     * Sets the size of the apparel item.
     * @param size New size value
     */
    void setSize(String size);
    
    /**
     * Sets the color of the apparel item.
     * @param color New color value
     */
    void setColor(String color);
    
    /**
     * Sets the brand of the apparel item.
     * @param brand New brand value
     */
    void setBrand(String brand);
}

package model.items;

/**
 * Defines the interface for electronic items in the marketplace.
 * Specifies electronic-specific properties and operations.
 * 
 * @author Youssef Abdelkader
 * @author Anthony Kim  
 * @author Caroline Murphy
 * @author Eric Yen
 * @author Isaac Yoon
 * @version April 20 2025
 */
public interface IElectronic {
    /**
     * @return The type of electronic item
     */
    String getType();
    
    /**
     * @return The manufacturing year of the electronic item
     */
    int getYear();
    
    /**
     * Sets the type of electronic item.
     * @param type New type value
     */
    void setType(String type);
    
    /**
     * Sets the manufacturing year of the electronic item.
     * @param year New year value
     */
    void setYear(int year);
}

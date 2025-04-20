package model.items;

/**
 * Defines the interface for vehicle items in the marketplace.
 * Specifies vehicle-specific properties and operations.
 */
public interface IVehicle {
    /**
     * @return The vehicle's current mileage
     */
    int getMileage();
    
    /**
     * @return The vehicle's manufacturing year
     */
    int getYear();
    
    /**
     * @return The vehicle's brand
     */
    String getBrand();
    
    /**
     * Sets the vehicle's mileage.
     * @param mileage New mileage value
     */
    void setMileage(int mileage);
    
    /**
     * Sets the vehicle's manufacturing year.
     * @param year New year value
     */
    void setYear(int year);
    
    /**
     * Sets the vehicle's brand.
     * @param brand New brand value
     */
    void setBrand(String brand);
}

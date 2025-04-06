package model.items;

/**
 * Interface for vehicle items
 */

public interface IVehicle {
    int getMileage();
    int getYear();
    String getBrand();
    void setMileage(int mileage);
    void setYear(int year);
    void setBrand(String brand);
}

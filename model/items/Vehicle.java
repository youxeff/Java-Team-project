package model.items;
import model.users.User;

/**
 * Represents a vehicle item in the marketplace.
 * Extends AbstractItem and implements IVehicle interface.
 * 
 * @author Youssef Abdelkader
 * @author Anthony Kim  
 * @author Caroline Murphy
 * @author Eric Yen
 * @author Isaac Yoon
 * @version April 20 2025
 */
public class Vehicle extends AbstractItem implements IVehicle {
    private int mileage;
    private int year;
    private String brand;
    private final Object lock = new Object();

    /**
     * Constructs a new Vehicle item
     * @param name The name of the vehicle
     * @param cost The price of the vehicle
     * @param soldBy The user selling the vehicle
     * @param image Image path/URL for the vehicle
     * @param category Category of the vehicle
     * @param mileage Mileage of the vehicle
     * @param year Manufacturing year of the vehicle
     * @param brand Brand of the vehicle
     */
    public Vehicle(String name, double cost, User soldBy, String image,
                   String category, int mileage, int year, String brand) {
        super(name, cost, soldBy, image, category);
        this.mileage = mileage;
        this.year = year;
        this.brand = brand;
    }

    @Override
    public int getMileage() {
        synchronized (lock) {
            return mileage;
        }
    }
    
    @Override
    public int getYear() {
        synchronized (lock) {
            return year;
        }
    }
    
    @Override
    public String getBrand() {
        synchronized (lock) {
            return brand;
        }
    }

    @Override
    public void setMileage(int mileage) {
        synchronized (lock) {
            this.mileage = mileage;
        }
    }
    
    @Override
    public void setYear(int year) {
        synchronized (lock) {
            this.year = year;
        }
    }
    
    @Override
    public void setBrand(String brand) {
        synchronized (lock) {
            this.brand = brand;
        }
    }

    @Override
    public String toString() {
        synchronized (lock) {
            return super.toString() + String.format(" - Mileage: %d - Year: %d - Brand: %s",
                    mileage, year, brand);
        }
    }
}

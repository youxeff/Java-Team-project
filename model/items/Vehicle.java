package model.items;
import model.users.User;

/**
 * Represents a vehicle item in the marketplace.
 * Extends AbstractItem and implements IVehicle interface.
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
    public synchronized int getMileage() { return mileage; }
    @Override
    public synchronized int getYear() { return year; }
    @Override
    public synchronized String getBrand() { return brand; }

    @Override
    public synchronized void setMileage(int mileage) { this.mileage = mileage; }
    @Override
    public synchronized void setYear(int year) { this.year = year; }
    @Override
    public synchronized void setBrand(String brand) { this.brand = brand; }

    @Override
    public synchronized String toString() {
        return super.toString() + String.format(" - Mileage: %d - Year: %d - Brand: %s",
                mileage, year, brand);
    }
}

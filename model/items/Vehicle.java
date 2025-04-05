package model.items;
import model.users.User;

public class Vehicle extends AbstractItem {
    private int mileage;
    private int year;
    private String brand;
    private final Object lock = new Object();

    public Vehicle(String name, double cost, User soldBy, String image,
                   String category, int mileage, int year, String brand) {
        super(name, cost, soldBy, image, category);
        this.mileage = mileage;
        this.year = year;
        this.brand = brand;
    }

    // IVehicle specific methods
    public synchronized int getMileage() { return mileage; }
    public synchronized int getYear() { return year; }
    public synchronized String getBrand() { return brand; }

    public synchronized void setMileage(int mileage) { this.mileage = mileage; }
    public synchronized void setYear(int year) { this.year = year; }
    public synchronized void setBrand(String brand) { this.brand = brand; }

    @Override
    public synchronized String toString() {
        return super.toString() + String.format(" - Mileage: %d - Year: %d - Brand: %s",
                mileage, year, brand);
    }
}

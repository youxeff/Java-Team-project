public class Vehicle extends AbstractItem {
    private int mileage;
    private int year;
    private String brand;

    public Vehicle(String name, double cost, User soldBy, String image,
                   String category, int mileage, int year, String brand) {
        super(name, cost, soldBy, image, category);
        this.mileage = mileage;
        this.year = year;
        this.brand = brand;
    }

    // IVehicle specific methods
    public int getMileage() { return mileage; }
    public int getYear() { return year; }
    public String getBrand() { return brand; }

    public void setMileage(int mileage) { this.mileage = mileage; }
    public void setYear(int year) { this.year = year; }
    public void setBrand(String brand) { this.brand = brand; }

    @Override
    public String toString() {
        return super.toString() + String.format(" - Mileage: %d - Year: %d - Brand: %s",
                mileage, year, brand);
    }
}

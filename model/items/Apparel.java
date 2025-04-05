package model.items;
import model.users.User;
import model.items.AbstractItem;

public class Apparel extends AbstractItem {
    private String size;
    private String color;
    private String brand;
    private final Object lock = new Object();

    public Apparel(String name, double cost, User soldBy, String image,
                   String category, String size, String color, String brand) {
        super(name, cost, soldBy, image, category);
        this.size = size;
        this.color = color;
        this.brand = brand;
    }

    // IApparel specific methods
    public synchronized String getSize() { return size; }
    public synchronized String getColor() { return color; }
    public synchronized String getBrand() { return brand; }

    public synchronized void setSize(String size) { this.size = size; }
    public synchronized void setColor(String color) { this.color = color; }
    public synchronized void setBrand(String brand) { this.brand = brand; }

    @Override
    public synchronized String toString() {
        return super.toString() + String.format(" - Size: %s - Color: %s - Brand: %s",
                size, color, brand);
    }
}

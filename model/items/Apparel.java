package model.items;
import model.users.User;

/**
 * Represents an apparel item in the marketplace.
 * Extends AbstractItem and implements IApparel interface.
 */
public class Apparel extends AbstractItem implements IApparel {
    private String size;
    private String color;
    private String brand;
    private final Object lock = new Object();

    /**
     * Constructs a new Apparel item
     * @param name The name of the apparel
     * @param cost The price of the apparel
     * @param soldBy The user selling the apparel
     * @param image Image path/URL for the apparel
     * @param category Category of the apparel
     * @param size Size of the apparel
     * @param color Color of the apparel
     * @param brand Brand of the apparel
     */
    public Apparel(String name, double cost, User soldBy, String image,
                   String category, String size, String color, String brand) {
        super(name, cost, soldBy, image, category);
        this.size = size;
        this.color = color;
        this.brand = brand;
    }

    @Override
    public String getSize() {
        synchronized(lock) {
            return size;
        }
    }
    
    @Override
    public String getColor() {
        synchronized(lock) {
            return color;
        }
    }
    
    @Override
    public String getBrand() {
        synchronized(lock) {
            return brand;
        }
    }

    @Override
    public void setSize(String size) {
        synchronized(lock) {
            this.size = size;
        }
    }
    
    @Override
    public void setColor(String color) {
        synchronized(lock) {
            this.color = color;
        }
    }
    
    @Override
    public void setBrand(String brand) {
        synchronized(lock) {
            this.brand = brand;
        }
    }

    @Override
    public String toString() {
        synchronized(lock) {
            return super.toString() + String.format(" - Size: %s - Color: %s - Brand: %s",
                    size, color, brand);
        }
    }
}

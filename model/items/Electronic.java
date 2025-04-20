package model.items;
import model.users.User;

/**
 * Represents an electronic item in the marketplace.
 * Extends AbstractItem and implements IElectronic interface.
 *
 * @author Youssef Abdelkader
 * @author Anthony Kim  
 * @author Caroline Murphy
 * @author Eric Yen
 * @author Isaac Yoon
 * @version April 20 2025
 */
public class Electronic extends AbstractItem implements IElectronic {
    private String type;
    private int year;
    private final Object LOCK = new Object();

    /**
     * Constructs a new Electronic item
     * @param name The name of the electronic item
     * @param cost The price of the electronic item
     * @param soldBy The user selling the electronic item
     * @param image Image path/URL for the electronic item
     * @param category Category of the electronic item
     * @param type Type of the electronic item
     * @param year Manufacturing year of the electronic item
     */
    public Electronic(String name, double cost, User soldBy, String image,
                     String category, String type, int year) {
        super(name, cost, soldBy, image, category);
        this.type = type;
        this.year = year;
    }

    @Override
    public String getType() {
        synchronized (LOCK) {
            return type;
        }
    }
    
    @Override
    public int getYear() {
        synchronized (LOCK) {
            return year;
        }
    }

    @Override
    public void setType(String type) {
        synchronized (LOCK) {
            this.type = type;
        }
    }
    
    @Override
    public void setYear(int year) {
        synchronized (LOCK) {
            this.year = year;
        }
    }

    @Override
    public String toString() {
        synchronized (LOCK) {
            return super.toString() + String.format(" - Brand: %s - Year: %d", type, year);
        }
    }
}

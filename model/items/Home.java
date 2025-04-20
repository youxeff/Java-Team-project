package model.items;
import model.users.User;

/**
 * Represents a home item in the marketplace.
 * Extends AbstractItem and implements IHome interface.
 *
 * @author Youssef Abdelkader
 * @author Anthony Kim  
 * @author Caroline Murphy
 * @author Eric Yen
 * @author Isaac Yoon
 * @version April 20 2025
 */
public class Home extends AbstractItem implements IHome {
    private String type;
    private final Object LOCK = new Object();

    /**
     * Constructs a new Home item
     * @param name The name of the home item
     * @param cost The price of the home item
     * @param soldBy The user selling the home item
     * @param image Image path/URL for the home item
     * @param category Category of the home item
     * @param type Type of the home item
     */
    public Home(String name, double cost, User soldBy, String image,
                String category, String type) {
        super(name, cost, soldBy, image, category);
        this.type = type;
    }

    @Override
    public String getType() {
        synchronized (LOCK) {
            return type;
        }
    }

    @Override
    public void setType(String type) {
        synchronized (LOCK) {
            this.type = type;
        }
    }

    @Override
    public String toString() {
        synchronized (LOCK) {
            return super.toString() + String.format(" - Type: %s", type);
        }
    }
}

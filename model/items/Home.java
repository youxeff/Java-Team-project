package model.items;
import model.users.User;

/**
 * Represents a home item in the marketplace.
 * Extends AbstractItem and implements IHome interface.
 */
public class Home extends AbstractItem implements IHome {
    private String type;
    private final Object lock = new Object();

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
    public synchronized String getType() { return type; }

    @Override
    public synchronized void setType(String type) { this.type = type; }

    @Override
    public synchronized String toString() {
        return super.toString() + String.format(" - Type: %s", type);
    }
}

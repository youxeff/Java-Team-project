package model.items;
import model.users.User;

/**
 * Represents a collectible item in the marketplace.
 * Extends AbstractItem and implements ICollectible interface.
 */
public class Collectible extends AbstractItem implements ICollectible {
    private String type;
    private String condition;
    private final Object lock = new Object();

    /**
     * Constructs a new Collectible item
     * @param name The name of the collectible
     * @param cost The price of the collectible
     * @param soldBy The user selling the collectible
     * @param image Image path/URL for the collectible
     * @param category Category of the collectible
     * @param type Type of the collectible
     * @param condition Condition of the collectible
     */
    public Collectible(String name, double cost, User soldBy, String image,
                       String category, String type, String condition) {
        super(name, cost, soldBy, image, category);
        this.type = type;
        this.condition = condition;
    }

    @Override
    public synchronized String getType() { return type; }
    @Override
    public synchronized String getCondition() { return condition; }

    @Override
    public synchronized void setType(String type) { this.type = type; }
    @Override
    public synchronized void setCondition(String condition) { this.condition = condition; }

    @Override
    public synchronized String toString() {
        return super.toString() + String.format(" - Type: %s - Condition: %s", type, condition);
    }
}

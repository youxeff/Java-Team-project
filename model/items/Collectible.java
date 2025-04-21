package model.items;
import model.users.User;

/**
 * Represents a collectible item in the marketplace.
 * Extends AbstractItem and implements ICollectible interface.
 *
 * @author Youssef Abdelkader
 * @author Anthony Kim  
 * @author Caroline Murphy
 * @author Eric Yen
 * @author Isaac Yoon
 * @version April 20 2025
 */
public class Collectible extends AbstractItem implements ICollectible {
    private String type;
    private String condition;
    private final Object lOCK = new Object();

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
    public String getType() {
        synchronized (lOCK) {
            return type;
        }
    }
    
    @Override
    public String getCondition() {
        synchronized (lOCK) {
            return condition;
        }
    }

    @Override
    public void setType(String type) {
        synchronized (lOCK) {
            this.type = type;
        }
    }
    
    @Override
    public void setCondition(String condition) {
        synchronized (lOCK) {
            this.condition = condition;
        }
    }

    @Override
    public String toString() {
        synchronized (lOCK) {
            return super.toString() + String.format(" - Type: %s - Condition: %s",
                    type, condition);
        }
    }
}

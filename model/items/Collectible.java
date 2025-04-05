
package model.items;
import model.users.User;
public class Collectible extends AbstractItem {
    private String type;
    private String condition;
    private final Object lock = new Object();

    public Collectible(String name, double cost, User soldBy, String image,
                       String category, String type, String condition) {
        super(name, cost, soldBy, image, category);
        this.type = type;
        this.condition = condition;
    }

    // ICollectibles specific methods
    public synchronized String getType() { return type; }
    public synchronized String getCondition() { return condition; }

    public synchronized void setType(String type) { this.type = type; }
    public synchronized void setCondition(String condition) { this.condition = condition; }

    @Override
    public synchronized String toString() {
        return super.toString() + String.format(" - Type: %s - Condition: %s", type, condition);
    }
}

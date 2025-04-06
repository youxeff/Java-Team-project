package model.items;
import java.io.Serializable;
import java.util.ArrayList;
import model.users.User;

/**
 * Abstract base class for all items in the marketplace.
 * Implements the Item interface and provides common functionality.
 */
public abstract class AbstractItem implements Item, Serializable {
    private static final long serialVersionUID = 1L;

    protected String name;
    protected double cost;
    protected User soldBy;
    protected boolean isAvailable;
    protected String image;
    protected String category;

    private final Object lock = new Object();

    /**
     * Constructs a new AbstractItem
     * @param name The name of the item
     * @param cost The price of the item
     * @param soldBy The user selling the item
     * @param image Image path/URL for the item
     * @param category Category of the item
     */
    public AbstractItem(String name, double cost, User soldBy, String image, String category) {
        this.name = name;
        this.cost = cost;
        this.soldBy = soldBy;
        this.isAvailable = true;
        this.image = image;
        this.category = category;
    }

    @Override
    public synchronized Boolean sellItem(User user) {
        if (!isAvailable || user.getBalance() < cost) {
            return false;
        }
        user.setBalance(user.getBalance() - cost);
        soldBy.setBalance(soldBy.getBalance() + cost);
        markSold();
        return true;
    }

    @Override
    public synchronized Boolean deleteItem() {
        isAvailable = false;
        return true;
    }

    @Override
    public ArrayList<Item> searchByName(String name) {
        // This would be implemented in the marketplace class
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Item> searchByCategory(String category) {
        // This would be implemented in the marketplace class
        return new ArrayList<>();
    }

    @Override
    public synchronized void markSold() {
        isAvailable = false;
    }

    @Override
    public synchronized String toString() {
        return String.format("%s - $%.2f - Sold by: %s - %s",
                name, cost, soldBy.getUserName(), isAvailable ? "Available" : "Sold");
    }

    @Override
    public synchronized String getName() { return name; }
    @Override
    public synchronized double getCost() { return cost; }
    @Override
    public synchronized User getSoldBy() { return soldBy; }
    @Override
    public synchronized boolean isAvailable() { return isAvailable; }
    @Override
    public synchronized String getImage() { return image; }
    @Override
    public synchronized String getCategory() { return category; }
}

import java.util.ArrayList;

public abstract class AbstractItem implements Item {
    protected String name;
    protected double cost;
    protected User soldBy;
    protected boolean isAvailable;
    protected String image;
    protected String category;

    public AbstractItem(String name, double cost, User soldBy, String image, String category) {
        this.name = name;
        this.cost = cost;
        this.soldBy = soldBy;
        this.isAvailable = true;
        this.image = image;
        this.category = category;
    }

    @Override
    public Boolean sellItem(User user) {
        if (!isAvailable || user.getBalance() < cost) {
            return false;
        }
        user.setBalance(user.getBalance() - cost);
        soldBy.setBalance(soldBy.getBalance() + cost);
        markSold();
        return true;
    }

    @Override
    public Boolean deleteItem() {
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
    public void markSold() {
        isAvailable = false;
    }

    @Override
    public String toString() {
        return String.format("%s - $%.2f - Sold by: %s - %s",
                name, cost, soldBy.getUserName(), isAvailable ? "Available" : "Sold");
    }

    // Common getters and setters
    public String getName() { return name; }
    public double getCost() { return cost; }
    public User getSoldBy() { return soldBy; }
    public boolean isAvailable() { return isAvailable; }
    public String getImage() { return image; }
    public String getCategory() { return category; }
}

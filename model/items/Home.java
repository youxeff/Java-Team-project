package model.items;


import model.users.User;

public class Home extends AbstractItem {
    private String type;
    private final Object lock = new Object();

    public Home(String name, double cost, User soldBy, String image,
                String category, String type) {
        super(name, cost, soldBy, image, category);
        this.type = type;
    }

    // home specific methods
    public synchronized String getType() { return type; }

    public synchronized void setType(String type) { this.type = type; }

    @Override
    public synchronized String toString() {
        return super.toString() + String.format(" - Type: %s", type);
    }
}

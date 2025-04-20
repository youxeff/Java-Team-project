package model.items;
import java.io.Serializable;
import java.util.ArrayList;
import model.users.User;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Abstract base class for all items in the marketplace.
 * Implements the Item interface and provides common functionality.
 * 
 * @author Youssef Abdelkader
 * @author Anthony Kim  
 * @author Caroline Murphy
 * @author Eric Yen
 * @author Isaac Yoon
 * @version April 20 2025
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
        
        // Calculate new balances
        double buyerNewBalance = user.getBalance() - cost;
        double sellerNewBalance = soldBy.getBalance() + cost;
        
        // Update balances
        user.setBalance(buyerNewBalance);
        soldBy.setBalance(sellerNewBalance);
        
        // Update the balances in the users.txt file
        try {
            File file = new File("users.txt");
            List<String> lines = Files.readAllLines(file.toPath());
            List<String> updatedLines = new ArrayList<>();

            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    if (parts[0].equals(user.getUserName())) {
                        // Update buyer's balance
                        String updatedLine = String.format("%s,%s,%s,%s,%.2f",
                                parts[0], parts[1], parts[2], parts[3], buyerNewBalance);
                        updatedLines.add(updatedLine);
                    } else if (parts[0].equals(soldBy.getUserName())) {
                        // Update seller's balance
                        String updatedLine = String.format("%s,%s,%s,%s,%.2f",
                                parts[0], parts[1], parts[2], parts[3], sellerNewBalance);
                        updatedLines.add(updatedLine);
                    } else {
                        updatedLines.add(line);
                    }
                } else {
                    updatedLines.add(line);
                }
            }

            Files.write(file.toPath(), updatedLines);
        } catch (IOException e) {
            System.err.println("Error updating balances in file: " + e.getMessage());
            return false;
        }

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

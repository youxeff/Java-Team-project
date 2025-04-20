package model.items;
import java.io.Serializable;
import java.util.ArrayList;
import model.users.User;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import Service.Marketplace;

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
    private static final long SERIAL_VERSION_UID = 1L;

    protected String name;
    protected double cost;
    protected User soldBy;
    protected boolean isAvailable;
    protected String image;
    protected String category;

    private final Object LOCK = new Object();

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
    public Boolean sellItem(User user) {
        synchronized (LOCK) {
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
    }

    @Override
    public Boolean deleteItem() {
        synchronized (LOCK) {
            isAvailable = false;
            return true;
        }
    }

    @Override
    public void markSold() {
        synchronized (LOCK) {
            isAvailable = false;
        }
    }

    @Override
    public String toString() {
        synchronized (LOCK) {
            return String.format("%s - $%.2f - Sold by: %s - %s",
                    name, cost, soldBy.getUserName(), isAvailable ? "Available" : "Sold");
        }
    }

    @Override
    public String getName() { 
        synchronized (LOCK) { 
            return name; 
        }
    }
    
    @Override
    public double getCost() { 
        synchronized (LOCK) { 
            return cost; 
        }
    }
    
    @Override
    public User getSoldBy() { 
        synchronized (LOCK) { 
            return soldBy; 
        }
    }
    
    @Override
    public boolean isAvailable() { 
        synchronized (LOCK) { 
            return isAvailable; 
        }
    }
    
    @Override
    public String getImage() { 
        synchronized (LOCK) { 
            return image; 
        }
    }
    
    @Override
    public String getCategory() { 
        synchronized (LOCK) { 
            return category; 
        }
    }

    @Override
    public ArrayList<Item> searchByName(String searchName) {
        // Delegate search functionality to marketplace
        return new Marketplace().searchByName(searchName);
    }

    @Override
    public ArrayList<Item> searchByCategory(String searchCategory) {
        // Delegate search functionality to marketplace
        return new Marketplace().searchByCategory(searchCategory);
    }
}

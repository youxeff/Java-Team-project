package Service;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import model.items.*;
import model.users.MarketplaceUser;
import model.users.User;

/**
 * The Marketplace class implements the IMarketplace interface and serves as the core service
 * for managing users, items, and transactions in the marketplace system.
 *
 * @author Youssef Abdelkader
 * @author Anthony Kim  
 * @author Caroline Murphy
 * @author Eric Yen
 * @author Isaac Yoon
 * @version April 20 2025
 */
public class Marketplace implements IMarketplace {
    private ArrayList<User> users;
    private final ArrayList<Item> items;
    private static final String USERS_FILE = "users.txt";
    private static final String ITEMS_FILE = "items.txt";
    private static final String TRANSACTIONS_FILE = "transactions.txt";
    private final Object lock = new Object();

    /**
     * Constructs a new Marketplace instance and initializes the system by loading
     * existing user and item data from files.
     * 
     * If the data files don't exist, they will be created. Any IO errors during
     * initialization will be printed to stderr but won't prevent the marketplace
     * from being created.
     */
    public Marketplace() {
        this.users = new ArrayList<>();
        this.items = new ArrayList<>();
        try {
            initializeUserDataFile();
            loadAllUsers();
            loadAllItems();
        } catch (IOException e) {
            System.err.println("Error initializing marketplace: " + e.getMessage());
        }
    }

    /**
     * Updates the user data file with a new or existing user's information.
     * 
     * @param user The user to be added or updated in the system
     * @return true if the operation was successful, false otherwise
     * @throws IOException if there's an error writing to the user data file
     */
    @Override
    public synchronized boolean updateUserData(User user) throws IOException {
        synchronized (lock) {
            if (!users.contains(user)) {
                users.add(user);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
                String userData = String.format("%s,%s,%s,%s,%.2f",
                        user.getUserName(),
                        user.getPassword(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getBalance());
                writer.write(userData);
                writer.newLine();
                return true;
            }
        }
    }

    /**
     * Initializes the user data file if it doesn't already exist.
     * 
     * @return true if the file was created or already exists, false if creation failed
     * @throws IOException if there's an error creating the file
     */
    @Override
    public synchronized boolean initializeUserDataFile() throws IOException {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            return file.createNewFile();
        }
        return true;
    }

    /**
     * Loads all users from the user data file into memory.
     * 
     * @return An ArrayList containing all registered users
     * @throws IOException if there's an error reading the user data file
     */
    @Override
    public synchronized ArrayList<User> loadAllUsers() throws IOException {
        ArrayList<User> loadedUsers = new ArrayList<>();
        synchronized (lock) {
            try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 5) {
                        String userName = parts[0];
                        String password = parts[1];
                        String firstName = parts[2];
                        String lastName = parts[3];
                        double balance = Double.parseDouble(parts[4]);

                        MarketplaceUser user = new MarketplaceUser(
                            firstName, 
                            lastName, 
                            userName, 
                            password, 
                            balance, 
                            true);
                        loadedUsers.add(user);
                    }
                }
            }
            users = loadedUsers;
            return loadedUsers;
        }
    }

    /**
     * Adds a new item to the marketplace and persists it to the items file.
     * 
     * @param item The item to be added to the marketplace
     */
    public synchronized void addItem(Item item) {
        synchronized (lock) {
            items.add(item);
            saveItemToFile(item);
        }
    }

    /**
     * Saves an item to the items data file.
     * 
     * @param item The item to be saved
     */
    private synchronized void saveItemToFile(Item item) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ITEMS_FILE, true))) {
            writer.write(itemToString(item));
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving item: " + e.getMessage());
        }
    }

    /**
     * Converts an item to a string representation for file storage.
     * 
     * @param item The item to convert
     * @return A comma-separated string representing the item
     */
    private String itemToString(Item item) {
        String className = item.getClass().getSimpleName();
        String base = String.format("%s,%s,%.2f,%s,%b,%s,%s",
                className,
                item.getName(),
                item.getCost(),
                item.getSoldBy().getUserName(),
                item.isAvailable(),
                item.getImage(),
                item.getCategory());

        if (item instanceof Electronic) {
            Electronic e = (Electronic) item;
            return String.format("%s,%s,%d", base, e.getType(), e.getYear());
        } else if (item instanceof Apparel) {
            Apparel a = (Apparel) item;
            return String.format("%s,%s,%s,%s", base, a.getSize(), a.getColor(), a.getBrand());
        } else if (item instanceof Home) {
            Home h = (Home) item;
            return String.format("%s,%s", base, h.getType());
        } else if (item instanceof Vehicle) {
            Vehicle v = (Vehicle) item;
            return String.format("%s,%d,%d,%s", base, v.getMileage(), v.getYear(), v.getBrand());
        } else if (item instanceof Collectible) {
            Collectible c = (Collectible) item;
            return String.format("%s,%s,%s", base, c.getType(), c.getCondition());
        }
        return base;
    }

    /**
     * Loads all items from the items data file into memory.
     * 
     * @throws IOException if there's an error reading the items file
     */
    private synchronized void loadAllItems() throws IOException {
        File file = new File(ITEMS_FILE);
        if (!file.exists()) {
            file.createNewFile();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ITEMS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Item item = parseItem(line);
                if (item != null) {
                    items.add(item);
                }
            }
        }
    }

    /**
     * Parses a line from the items file into an Item object.
     * 
     * @param line The line from the items file to parse
     * @return The parsed Item object, or null if parsing fails
     */
    private Item parseItem(String line) {
        String[] parts = line.split(",");
        if (parts.length < 7) return null;

        String className = parts[0];
        String name = parts[1];
        double cost = Double.parseDouble(parts[2]);
        String sellerUsername = parts[3];
        boolean isAvailable = Boolean.parseBoolean(parts[4]);
        String image = parts[5];
        String category = parts[6];

        User seller = users.stream()
                .filter(u -> u.getUserName().equals(sellerUsername))
                .findFirst()
                .orElse(null);

        if (seller == null) return null;

        Item item = null;
        try {
            switch (className) {
                case "Electronic":
                    item = new Electronic(name, cost, seller, image, category, parts[7], Integer.parseInt(parts[8]));
                    break;
                case "Apparel":
                    item = new Apparel(name, cost, seller, image, category, parts[7], parts[8], parts[9]);
                    break;
                case "Home":
                    item = new Home(name, cost, seller, image, category, parts[7]);
                    break;
                case "Vehicle":
                    item = new Vehicle(name, cost, seller, image, category,
                            Integer.parseInt(parts[7]), Integer.parseInt(parts[8]), parts[9]);
                    break;
                case "Collectible":
                    item = new Collectible(name, cost, seller, image, category, parts[7], parts[8]);
                    break;
                default:
                    return null;
            }
            if (item != null && !isAvailable) {
                item.markSold();
            }
            return item;
        } catch (Exception e) {
            System.err.println("Error parsing item: " + e.getMessage());
            return null;
        }
    }

    /**
     * Searches for sellers by username, first name, or last name (case-insensitive).
     * 
     * @param sellerSearch The search string to match against seller information
     * @return A list of matching sellers
     */
    @Override
    public synchronized ArrayList<User> searchSeller(String sellerSearch) {
        ArrayList<User> result = new ArrayList<>();
        synchronized (lock) {
            String searchLower = sellerSearch.toLowerCase();

            for (User user : users) {
                if (user.getUserName().toLowerCase().contains(searchLower) ||
                        user.getFirstName().toLowerCase().contains(searchLower) ||
                        user.getLastName().toLowerCase().contains(searchLower)) {
                    result.add(user);
                }
            }
        }
        return result;
    }

    /**
     * Searches for items by name (case-insensitive).
     * 
     * @param nameSearch The search string to match against item names
     * @return A list of matching items
     */
    @Override
    public synchronized ArrayList<Item> searchByName(String nameSearch) {
        ArrayList<Item> result = new ArrayList<>();
        synchronized (lock) {
            String searchLower = nameSearch.toLowerCase();

            for (Item item : items) {
                if (item.getName().toLowerCase().contains(searchLower)) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    /**
     * Searches for items by category (case-insensitive exact match).
     * 
     * @param categorySearch The category to search for
     * @return A list of items in the specified category
     */
    @Override
    public synchronized ArrayList<Item> searchByCategory(String categorySearch) {
        ArrayList<Item> result = new ArrayList<>();
        synchronized (lock) {
            for (Item item : items) {
                if (item.getCategory().equalsIgnoreCase(categorySearch)) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    /**
     * Authenticates a user based on username and password.
     * 
     * @param username The username to authenticate
     * @param password The password to verify
     * @return The authenticated User object if successful, null otherwise
     */
    public synchronized User authenticateUser(String username, String password) {
        synchronized (lock) {
            return users.stream()
                    .filter(user -> user.getUserName().equals(username) && user.getPassword().equals(password))
                    .findFirst()
                    .orElse(null);
        }
    }

    /**
     * Retrieves all available (unsold) items in the marketplace.
     * 
     * @return A list of all available items
     */
    public synchronized ArrayList<Item> getAvailableItems() {
        ArrayList<Item> result = new ArrayList<>();
        synchronized (lock) {
            for (Item item : items) {
                if (item.isAvailable()) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    /**
     * Processes a purchase transaction for an item.
     * 
     * @param item The item to be purchased
     * @param buyer The user purchasing the item
     * @return true if the purchase was successful, false otherwise
     */
    public synchronized boolean purchaseItem(Item item, User buyer) {
        synchronized (lock) {
            if (item.sellItem(buyer)) {
                String date = java.time.LocalDate.now().toString();
                saveTransaction(item, buyer, date);
                rewriteItemsFile();
                return true;
            }
            return false;
        }
    }

    /**
     * Rewrites the entire items file with current item data.
     */
    private synchronized void rewriteItemsFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ITEMS_FILE))) {
            for (Item item : items) {
                writer.write(itemToString(item));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error rewriting items file: " + e.getMessage());
        }
    }

    /**
     * Saves a transaction to the transactions file
     */
    private synchronized void saveTransaction(Item item, User buyer, String date) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE, true))) {
            String transaction = String.format("%s,%s,%s,%s,%s,%.2f,%s",
                buyer.getUserName(),
                item.getCategory(),
                item.getName(),
                item.getSoldBy().getUserName(),
                date,
                item.getCost(),
                item.getCategory());
            writer.write(transaction);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving transaction: " + e.getMessage());
        }
    }

    /**
     * Load transactions for a specific user
     */
    public synchronized ArrayList<String[]> loadTransactions(String username) {
        ArrayList<String[]> transactions = new ArrayList<>();
        try {
            File file = new File(TRANSACTIONS_FILE);
            if (!file.exists()) {
                return transactions;
            }

            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    // Add if user is either buyer or seller
                    if (parts[0].equals(username) || parts[3].equals(username)) {
                        transactions.add(parts);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading transactions: " + e.getMessage());
        }
        return transactions;
    }
}

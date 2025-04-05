import java.io.*;
import java.util.ArrayList;

public class Marketplace implements IMarketplace {
    private ArrayList<User> users;
    private ArrayList<Item> items;
    private static final String USERS_FILE = "users.txt";
    private static final String ITEMS_FILE = "items.txt";

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

    @Override
    public boolean updateUserData(User user) throws IOException {
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

    @Override
    public boolean initializeUserDataFile() throws IOException {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            return file.createNewFile();
        }
        return true;
    }

    @Override
    public ArrayList<User> loadAllUsers() throws IOException {
        ArrayList<User> loadedUsers = new ArrayList<>();
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

                    MarketplaceUser user = new MarketplaceUser(firstName, lastName, userName, password);
                    loadedUsers.add(user);
                }
            }
        }
        users = loadedUsers;
        return loadedUsers;
    }

    public void addItem(Item item) {
        items.add(item);
        saveItemToFile(item);
    }

    private void saveItemToFile(Item item) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ITEMS_FILE, true))) {
            writer.write(itemToString(item));
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving item: " + e.getMessage());
        }
    }

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

    private void loadAllItems() throws IOException {
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

        try {
            switch (className) {
                case "Electronic":
                    return new Electronic(name, cost, seller, image, category, parts[7], Integer.parseInt(parts[8]));
                case "Apparel":
                    return new Apparel(name, cost, seller, image, category, parts[7], parts[8], parts[9]);
                case "Home":
                    return new Home(name, cost, seller, image, category, parts[7]);
                case "Vehicle":
                    return new Vehicle(name, cost, seller, image, category,
                            Integer.parseInt(parts[7]), Integer.parseInt(parts[8]), parts[9]);
                case "Collectible":
                    return new Collectible(name, cost, seller, image, category, parts[7], parts[8]);
                default:
                    return null;
            }
        } catch (Exception e) {
            System.err.println("Error parsing item: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<User> searchSeller(String sellerSearch) {
        ArrayList<User> result = new ArrayList<>();
        String searchLower = sellerSearch.toLowerCase();

        for (User user : users) {
            if (user.getUserName().toLowerCase().contains(searchLower) ||
                    user.getFirstName().toLowerCase().contains(searchLower) ||
                    user.getLastName().toLowerCase().contains(searchLower)) {
                result.add(user);
            }
        }
        return result;
    }

    @Override
    public ArrayList<Item> searchByName(String nameSearch) {
        ArrayList<Item> result = new ArrayList<>();
        String searchLower = nameSearch.toLowerCase();

        for (Item item : items) {
            if (item.getName().toLowerCase().contains(searchLower)) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public ArrayList<Item> searchByCategory(String categorySearch) {
        ArrayList<Item> result = new ArrayList<>();

        for (Item item : items) {
            if (item.getCategory().equalsIgnoreCase(categorySearch)) {
                result.add(item);
            }
        }
        return result;
    }

    public User authenticateUser(String username, String password) {
        return users.stream()
                .filter(user -> user.getUserName().equals(username) && user.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public ArrayList<Item> getAvailableItems() {
        ArrayList<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (item.isAvailable()) {
                result.add(item);
            }
        }
        return result;
    }

    public boolean purchaseItem(Item item, User buyer) {
        if (item.sellItem(buyer)) {
            rewriteItemsFile();
            return true;
        }
        return false;
    }

    private void rewriteItemsFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ITEMS_FILE))) {
            for (Item item : items) {
                writer.write(itemToString(item));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error rewriting items file: " + e.getMessage());
        }
    }
}

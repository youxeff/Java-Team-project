import java.io.*;
import java.util.ArrayList;

// this is the main marketplace class that handles all the buying and selling
public class Marketplace implements IMarketplace {
    // list to store all users (both buyers and sellers)
    private ArrayList<User> users;
    // list to store all items for sale
    private ArrayList<Item> items;
    // file name where we save user data
    private static final String USERS_FILE = "users.txt";
    // file name where we save item data
    private static final String ITEMS_FILE = "items.txt";

    // constructor - runs when we create a new marketplace
    public Marketplace() {
        // create empty lists to start
        this.users = new ArrayList<>();
        this.items = new ArrayList<>();
        try {
            // set up the files we need
            initializeUserDataFile();
            // load all users from file
            loadAllUsers();
            // load all items from file
            loadAllItems();
        } catch (IOException e) {
            // if something goes wrong, print error message
            System.err.println("error starting marketplace: " + e.getMessage());
        }
    }

    // this method saves a user to our file
    @Override
    public boolean updateUserData(User user) throws IOException {
        // if user isn't already in our list, add them
        if (!users.contains(user)) {
            users.add(user);
        }

        // open file for writing (the 'true' means we add to end of file)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            // format the user data as a string with commas between values
            String userData = String.format("%s,%s,%s,%s,%s,%.2f",
                    user.getUserName(),
                    user.getPassword(),
                    user.getFirstName(),
                    user.getLastName(),
                    // check if user is seller or buyer
                    (user instanceof Seller) ? "SELLER" : "BUYER",
                    user.getBalance());
            // write the data to file
            writer.write(userData);
            // move to next line
            writer.newLine();
            return true;
        }
    }

    // this method creates the user file if it doesn't exist
    @Override
    public boolean initializeUserDataFile() throws IOException {
        File file = new File(USERS_FILE);
        // if file doesn't exist, create it
        if (!file.exists()) {
            return file.createNewFile();
        }
        return true;
    }

    // this method loads all users from the file
    @Override
    public ArrayList<User> loadAllUsers() throws IOException {
        // create empty list to hold users
        ArrayList<User> loadedUsers = new ArrayList<>();
        // open file for reading
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            // read each line one by one
            while ((line = reader.readLine()) != null) {
                // split line by commas
                String[] parts = line.split(",");
                // make sure we have enough parts
                if (parts.length >= 6) {
                    String userName = parts[0];
                    String password = parts[1];
                    String firstName = parts[2];
                    String lastName = parts[3];
                    String type = parts[4];
                    double balance = Double.parseDouble(parts[5]);

                    User user;
                    // create seller or buyer based on type
                    if (type.equals("SELLER")) {
                        user = new Seller(firstName, lastName, userName, password, balance);
                    } else {
                        user = new Buyer(firstName, lastName, userName, password, balance);
                    }
                    // add user to our list
                    loadedUsers.add(user);
                }
            }
        }
        // save the loaded users
        users = loadedUsers;
        return loadedUsers;
    }

    // this method adds a new item to the marketplace
    public void addItem(Item item) {
        // add to our list
        items.add(item);
        // save to file
        saveItemToFile(item);
    }

    // helper method to save an item to file
    private void saveItemToFile(Item item) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ITEMS_FILE, true))) {
            // convert item to string and write to file
            writer.write(itemToString(item));
            writer.newLine();
        } catch (IOException e) {
            System.err.println("error saving item: " + e.getMessage());
        }
    }

    // helper method to convert item to a string for saving
    private String itemToString(Item item) {
        // First get the class name safely
        String className;
        if (item instanceof Electronic) {
            className = "Electronic";
        } else if (item instanceof Apparel) {
            className = "Apparel";
        } else if (item instanceof Home) {
            className = "Home";
        } else if (item instanceof Vehicle) {
            className = "Vehicle";
        } else if (item instanceof Collectible) {
            className = "Collectible";
        } else {
            className = "Item"; // fallback
        }

        // Now format the string with the interface methods we know exist
        String base = String.format("%s,%s,%.2f,%s,%b,%s,%s",
                className,
                item.getName(),
                item.getCost(),
                item.getSoldBy().getUserName(),
                item.isAvailable(),
                item.getImage(),
                item.getCategory());

        // Handle the special fields based on type
        if (item instanceof Electronic) {
            Electronic e = (Electronic) item;
            return String.format("%s,%s,%d", base, e.getType(), e.getYear());
        } else if (item instanceof Apparel) {
            Apparel a = (Apparel) item;
            return String.format("%s,%s,%s,%s", base, a.getSize(), a.getColor(), a.getBrand());
        } else if (item instanceof Home) {
            Home h = (Home) item;
            return String.format("%s,%s,%s", base, h.getType());
        } else if (item instanceof Vehicle) {
            Vehicle v = (Vehicle) item;
            return String.format("%s,%d,%d,%s", base, v.getMileage(), v.getYear(), v.getBrand());
        } else if (item instanceof Collectible) {
            Collectible c = (Collectible) item;
            return String.format("%s,%s,%s", base, c.getType(), c.getCondition());
        }
        return base;
    }

    // this method loads all items from file
    private void loadAllItems() throws IOException {
        File file = new File(ITEMS_FILE);
        // if file doesn't exist, create it
        if (!file.exists()) {
            file.createNewFile();
            return;
        }

        // open file for reading
        try (BufferedReader reader = new BufferedReader(new FileReader(ITEMS_FILE))) {
            String line;
            // read each line
            while ((line = reader.readLine()) != null) {
                // convert line to item object
                Item item = parseItem(line);
                if (item != null) {
                    // add to our list
                    items.add(item);
                }
            }
        }
    }

    // helper method to convert a line of text to an item object :)
    private Item parseItem(String line) {
        // split the line by commas
        String[] parts = line.split(",");
        // check we have enough parts
        if (parts.length < 7) return null;

        // get basic item info
        String className = parts[0];
        String name = parts[1];
        double cost = Double.parseDouble(parts[2]);
        String sellerUsername = parts[3];
        boolean isAvailable = Boolean.parseBoolean(parts[4]);
        String image = parts[5];
        String category = parts[6];

        // find the seller in our user list
        Seller seller = null;
        for (User user : users) {
            if (user instanceof Seller && user.getUserName().equals(sellerUsername)) {
                seller = (Seller) user;
                break;
            }
        }

        // if we can't find seller, skip item
        if (seller == null) return null;

        try {
            // create the right kind of item based on the class name
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
            System.err.println("error reading item: " + e.getMessage());
            return null;
        }
    }

    // search for sellers by name (username, first name, or last name)
    @Override
    public ArrayList<Seller> searchSeller(String sellerSearch) {
        ArrayList<Seller> result = new ArrayList<>();
        // convert search term to lowercase for case-insensitive search
        String searchLower = sellerSearch.toLowerCase();

        // check each user
        for (User user : users) {
            // only look at sellers
            if (user instanceof Seller) {
                Seller seller = (Seller) user;
                // check if search term matches any part of seller's names
                if (seller.getUserName().toLowerCase().contains(searchLower) ||
                        seller.getFirstName().toLowerCase().contains(searchLower) ||
                        seller.getLastName().toLowerCase().contains(searchLower)) {
                    // if match, add to results
                    result.add(seller);
                }
            }
        }
        return result;
    }

    // search for items by name
    @Override
    public ArrayList<Item> searchByName(String nameSearch) {
        ArrayList<Item> result = new ArrayList<>();
        // make search case-insensitive
        String searchLower = nameSearch.toLowerCase();

        // check each item
        for (Item item : items) {
            // if item name contains search term
            if (item.getName().toLowerCase().contains(searchLower)) {
                // add to results
                result.add(item);
            }
        }
        return result;
    }

    // search for items by category
    @Override
    public ArrayList<Item> searchByCategory(String categorySearch) {
        ArrayList<Item> result = new ArrayList<>();

        // check each item
        for (Item item : items) {
            // if category matches exactly (case-insensitive)
            if (item.getCategory().equalsIgnoreCase(categorySearch)) {
                // add to results
                result.add(item);
            }
        }
        return result;
    }

    // get all items that are still available (not sold)
    public ArrayList<Item> getAvailableItems() {
        ArrayList<Item> result = new ArrayList<>();
        // check each item
        for (Item item : items) {
            // if available, add to list
            if (item.isAvailable()) {
                result.add(item);
            }
        }
        return result;
    }

    // handle buying an item
    public boolean purchaseItem(Item item, Buyer buyer) {
        // try to sell the item
        if (item.sellItem(buyer)) {
            // if successful, update our files
            rewriteItemsFile();
            return true;
        }
        // if failed
        return false;
    }

    // helper method to save all items back to file
    private void rewriteItemsFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ITEMS_FILE))) {
            // write each item to file
            for (Item item : items) {
                writer.write(itemToString(item));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("error saving items: " + e.getMessage());
        }
    }


}

package Main;
import Service.Marketplace;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import model.items.*;
import model.users.MarketplaceUser;
import model.users.User;

/**
 * The {@code Main} class serves as the entry point for the Marketplace system.
 * It provides a console-based user interface to register users, login, update balances,
 * view profiles, and buy or sell various items in the marketplace.
 **/

public class Main implements IMain, Runnable {
    private Scanner scanner = new Scanner(System.in);
    private MarketplaceUser currentUser = null;

    public static void main(String[] args) {
        new Main().run();
    }
    public void run() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Marketplace System ===");
            System.out.println("1. Register New User");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        registerUser();
                        break;
                    case 2:
                        loginUser();
                        break;
                    case 3:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        scanner.close();
        System.out.println("Goodbye!");
    }

    public void registerUser() {
        try {
            System.out.println("\n=== User Registration ===");
            System.out.print("Enter First Name: ");
            String firstName = scanner.nextLine();

            System.out.print("Enter Last Name: ");
            String lastName = scanner.nextLine();

            System.out.print("Enter Username: ");
            String username = scanner.nextLine();

            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            if (MarketplaceUser.loadUser(username) != null) {
                System.out.println("Username already exists.");
                return;
            }
            MarketplaceUser newUser = new MarketplaceUser(firstName, lastName, username, password);
            Marketplace marketplace = new Marketplace();
            marketplace.updateUserData(newUser);

            System.out.println("Registration successful!");

        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
        }
    }

    public void loginUser() {
        try {
            System.out.println("\n=== User Login ===");
            System.out.print("Enter Username: ");
            String username = scanner.nextLine();

            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            currentUser = MarketplaceUser.loadUser(username);
            if (currentUser != null && currentUser.verifyPassword(password)) {
                System.out.println("Login successful!");
                showUserMenu();
            } else {
                System.out.println("Login failed. Invalid credentials.");
            }
        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
        }
    }

    public void showUserMenu() {
        boolean userMenuRunning = true;
        while (userMenuRunning && currentUser != null) {
            System.out.println("\n=== User Menu ===");
            System.out.println("1. View Profile");
            System.out.println("2. Update Balance");
            System.out.println("3. Choose Buy or Sell");
            System.out.println("4. Send a Message");
            System.out.println("5. View Messages");

            System.out.println("6. Logout");
            System.out.print("Choose an option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        displayUserProfile();
                        break;
                    case 2:
                        updateBalance();
                        break;
                    case 3:
                        buyOrSell();
                        break;
                        case 4:
                        System.out.println("Who do u want to message?");
                        String recipientUsername = scanner.nextLine();
                        if (!MarketplaceUser.userExists(recipientUsername)) {
                            System.out.println("User \"" + recipientUsername + "\" does not exist.");
                            break;
                        }
                        System.out.println("What is your message");
                        String message = scanner.nextLine();
                        currentUser.sendMessageTo(recipientUsername, message);
                        break;
                    case 5:
                        currentUser.viewMessages();
                        break;
                    case 6:
                        userMenuRunning = false;
                        currentUser = null;
                        System.out.println("Logged out successfully.");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public void displayUserProfile() {
        System.out.println("\n=== User Profile ===");
        System.out.println("First Name: " + currentUser.getFirstName());
        System.out.println("Last Name: " + currentUser.getLastName());
        System.out.println("Username: " + currentUser.getUserName());
        System.out.println("Balance: $" + String.format("%.2f", currentUser.getBalance()));
    }

    public void updateBalance() {
        try {
            System.out.print("Enter new balance amount: $");
            double newBalance = Double.parseDouble(scanner.nextLine());
            currentUser.setBalance(newBalance);

            File file = new File("users.txt");
            List<String> lines = Files.readAllLines(file.toPath());
            List<String> updatedLines = new ArrayList<>();

            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[0].equals(currentUser.getUserName())) {
                    String updatedLine = String.format("%s,%s,%s,%s,%.2f",
                            currentUser.getUserName(),
                            currentUser.getPassword(),
                            currentUser.getFirstName(),
                            currentUser.getLastName(),
                            newBalance);
                    updatedLines.add(updatedLine);
                } else {
                    updatedLines.add(line);
                }
            }

            Files.write(file.toPath(), updatedLines);
            System.out.println("Balance updated successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        } catch (IOException e) {
            System.out.println("Error updating balance: " + e.getMessage());
        }
    }

    public void buyOrSell() {
        boolean buyOrSellMenu = true;
        while (buyOrSellMenu) {
            try {
                System.out.println("1. Sell");
                System.out.println("2. Buy");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        sellItem();
                        break;
                    case 2:
                        buyItem();
                        break;
                    case 3:
                        buyOrSellMenu = false;
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public void sellItem() {
        try {
            System.out.println("\n=== Add Item for Sale ===");

            System.out.println("Choose a category:");
            System.out.println("1. Apparel");
            System.out.println("2. Collectible");
            System.out.println("3. Electronic");
            System.out.println("4. Home");
            System.out.println("5. Vehicle");
            System.out.print("Enter category number: ");
            int category = Integer.parseInt(scanner.nextLine());

            Marketplace sellingItems = new Marketplace() {
                @Override
                public synchronized ArrayList<User> loadAllUsers() {
                    return new ArrayList<>();
                }
            };

            switch (category) {
                case 1:
                    String categoryName = "Apparel";
                    System.out.println("Enter name of Apparel: ");
                    String itemName = scanner.nextLine();
                    System.out.println("Enter cost of Apparel: ");
                    double itemCost = Double.parseDouble(scanner.nextLine());
                    System.out.println("Enter image of Apparel: ");
                    String itemImage = scanner.nextLine();
                    System.out.println("Enter size of Apparel: ");
                    String itemSize = scanner.nextLine();
                    System.out.println("Enter color of Apparel: ");
                    String itemColor = scanner.nextLine();
                    System.out.println("Enter brand of Apparel: ");
                    String itemBrand = scanner.nextLine();

                    Apparel apparel = new Apparel(itemName, itemCost, currentUser, itemImage,
                            categoryName, itemSize, itemColor, itemBrand);
                    sellingItems.addItem(apparel);

                    break;
                case 2:
                    categoryName = "Collectible";
                    System.out.println("Enter name of Collectible: ");
                    itemName = scanner.nextLine();
                    System.out.println("Enter cost of Collectible: ");
                    itemCost = Double.parseDouble(scanner.nextLine());
                    System.out.println("Enter image of Collectible: ");
                    itemImage = scanner.nextLine();
                    System.out.println("Enter type of Collectible: ");
                    String itemType = scanner.nextLine();
                    System.out.println("Enter condition of Collectible: ");
                    String itemCondition = scanner.nextLine();

                    Collectible collectile = new Collectible(itemName, itemCost, currentUser,
                            itemImage, categoryName, itemType, itemCondition);
                    sellingItems.addItem(collectile);

                    break;
                case 3:
                    categoryName = "Electronic";
                    System.out.println("Enter name of Electronic: ");
                    itemName = scanner.nextLine();
                    System.out.println("Enter cost of Electronic: ");
                    itemCost = Double.parseDouble(scanner.nextLine());
                    System.out.println("Enter image of Electronic: ");
                    itemImage = scanner.nextLine();
                    System.out.println("Enter type of Electronic: ");
                    itemType = scanner.nextLine();
                    System.out.println("Enter year of Electronic: ");
                    int itemYear = Integer.parseInt(scanner.nextLine());

                    Electronic electronic = new Electronic(itemName, itemCost, currentUser, itemImage,
                            categoryName, itemType, itemYear);
                    sellingItems.addItem(electronic);

                    break;
                case 4:
                    categoryName = "Home";
                    System.out.println("Enter name of Home: ");
                    itemName = scanner.nextLine();
                    System.out.println("Enter cost of Home: ");
                    itemCost = Double.parseDouble(scanner.nextLine());
                    System.out.println("Enter image of Home: ");
                    itemImage = scanner.nextLine();
                    System.out.println("Enter type of Home: ");
                    itemType = scanner.nextLine();

                    Home home = new Home(itemName, itemCost, currentUser, itemImage,
                            categoryName, itemType);
                    sellingItems.addItem(home);

                    break;
                case 5:
                    categoryName = "Vehicle";
                    System.out.println("Enter name of Vehicle: ");
                    itemName = scanner.nextLine();
                    System.out.println("Enter cost of Vehicle: ");
                    itemCost = Double.parseDouble(scanner.nextLine());
                    System.out.println("Enter image of Vehicle: ");
                    itemImage = scanner.nextLine();
                    System.out.println("Enter mileage of Vehicle: ");
                    int itemMileage = Integer.parseInt(scanner.nextLine());
                    System.out.println("Enter year of Vehicle: ");
                    itemYear = Integer.parseInt(scanner.nextLine());
                    System.out.println("Enter brand of Vehicle: ");
                    itemBrand = scanner.nextLine();

                    Vehicle vehicle = new Vehicle (itemName, itemCost, currentUser, itemImage,
                            categoryName, itemMileage, itemYear, itemBrand);
                    sellingItems.addItem(vehicle);

                    break;
                default:
                    System.out.println("Invalid category.");
                    return;
            }
            System.out.println("Item added successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    public void buyItem() {
        try {
            try {
                System.out.println("Choose a category: \n 1. Apparel\n 2. Collectible\n 3. Electronic\n 4. Home\n 5. Vehicle");
                int categoryChoice = Integer.parseInt(scanner.nextLine());

                String category = "";
                switch (categoryChoice) {
                    case 1:
                        category = "Apparel";
                        break;
                    case 2:
                        category = "Collectible";
                        break;
                    case 3:
                        category = "Electronic";
                        break;
                    case 4:
                        category = "Home";
                        break;
                    case 5:
                        category = "Vehicle";
                        break;
                    default:
                        System.out.println("Invalid category.");
                        return;
                }

                Marketplace marketplace = new Marketplace();
                ArrayList<Item> categoryItems = marketplace.searchByCategory(category);
                if (categoryItems.isEmpty()) {
                    System.out.println("No items found in this category.");
                    return;
                }

                System.out.println("\nAvailable Items:");
                for (int i = 0; i < categoryItems.size(); i++) {
                    Item item = categoryItems.get(i);
                    if (item.isAvailable()) {
                        System.out.printf("[%d] %s - $%.2f (Seller: %s)%n", i + 1,
                                item.getName(), item.getCost(), item.getSoldBy().getUserName());
                    }
                }

                System.out.print("\nEnter the number of the item to purchase: ");
                int itemIndex = Integer.parseInt(scanner.nextLine()) - 1;

                if (itemIndex < 0 || itemIndex >= categoryItems.size()) {
                    System.out.println("Invalid item number.");
                    return;
                }

                Item selectedItem = categoryItems.get(itemIndex);

                if (selectedItem.getCost() > currentUser.getBalance()) {
                    System.out.println("Insufficient balance to complete the purchase.");
                    return;
                }

                boolean success = marketplace.purchaseItem(selectedItem, currentUser);
                if (success) {
                    currentUser.setBalance(currentUser.getBalance() - selectedItem.getCost());
                    System.out.println("Purchase successful! Remaining Balance: $" + String.format("%.2f", currentUser.getBalance()));
                } else {
                    System.out.println("Purchase failed.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("Error during purchase: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }
}
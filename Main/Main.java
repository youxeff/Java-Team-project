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
            System.out.print("Enter First Name (or 'back' to return to menu): ");
            String firstName = scanner.nextLine();
            
            if (firstName.equalsIgnoreCase("back")) {
                return;
            }

            System.out.print("Enter Last Name (or 'back' to return to menu): ");
            String lastName = scanner.nextLine();
            
            if (lastName.equalsIgnoreCase("back")) {
                return;
            }

            System.out.print("Enter Username (or 'back' to return to menu): ");
            String username = scanner.nextLine();
            
            if (username.equalsIgnoreCase("back")) {
                return;
            }

            System.out.print("Enter Password (or 'back' to return to menu): ");
            String password = scanner.nextLine();
            
            if (password.equalsIgnoreCase("back")) {
                return;
            }

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
            System.out.print("Enter Username (or 'back' to return to menu): ");
            String username = scanner.nextLine();
            
            if (username.equalsIgnoreCase("back")) {
                return;
            }

            System.out.print("Enter Password (or 'back' to return to menu): ");
            String password = scanner.nextLine();
            
            if (password.equalsIgnoreCase("back")) {
                return;
            }

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
        System.out.printf("Seller Rating: %.1f (%d ratings)%n",
                currentUser.getAverageSellerRating(),
                currentUser.getNumberOfRatings());
    }

    public void updateBalance() {
        try {
            System.out.println("Current balance: $" + String.format("%.2f", currentUser.getBalance()));
            System.out.println("Enter amount to add (or type 'back' to return to menu): $");
            String input = scanner.nextLine();
            
            if (input.equalsIgnoreCase("back")) {
                return;
            }

            double amountToAdd = Double.parseDouble(input);
            double newBalance = currentUser.getBalance() + amountToAdd;
            currentUser.setBalance(newBalance);

            // Update the balance in the users.txt file
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

            // Write updates back to the file
            Files.write(file.toPath(), updatedLines);
            System.out.println("Balance updated successfully! New balance: $" + String.format("%.2f", newBalance));
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

            System.out.println("Choose a category (or type 'back' to return to menu):");
            System.out.println("1. Apparel");
            System.out.println("2. Collectible");
            System.out.println("3. Electronic");
            System.out.println("4. Home");
            System.out.println("5. Vehicle");
            System.out.print("Enter category number: ");
            String input = scanner.nextLine();
            
            if (input.equalsIgnoreCase("back")) {
                return;
            }
            
            int category = Integer.parseInt(input);

            Marketplace sellingItems = new Marketplace() {
                @Override
                public synchronized ArrayList<User> loadAllUsers() {
                    return new ArrayList<>();
                }
            };

            switch (category) {
                case 1:
                    String categoryName = "Apparel";
                    System.out.println("Enter name of Apparel (or 'back' to return to menu): ");
                    String itemName = scanner.nextLine();
                    if (itemName.equalsIgnoreCase("back")) return;

                    System.out.println("Enter cost of Apparel (or 'back' to return to menu): ");
                    String costInput = scanner.nextLine();
                    if (costInput.equalsIgnoreCase("back")) return;
                    double itemCost = Double.parseDouble(costInput);

                    System.out.println("Enter image of Apparel (or 'back' to return to menu): ");
                    String itemImage = scanner.nextLine();
                    if (itemImage.equalsIgnoreCase("back")) return;

                    System.out.println("Enter size of Apparel (or 'back' to return to menu): ");
                    String itemSize = scanner.nextLine();
                    if (itemSize.equalsIgnoreCase("back")) return;

                    System.out.println("Enter color of Apparel (or 'back' to return to menu): ");
                    String itemColor = scanner.nextLine();
                    if (itemColor.equalsIgnoreCase("back")) return;

                    System.out.println("Enter brand of Apparel (or 'back' to return to menu): ");
                    String itemBrand = scanner.nextLine();
                    if (itemBrand.equalsIgnoreCase("back")) return;

                    Apparel apparel = new Apparel(itemName, itemCost, currentUser, itemImage,
                            categoryName, itemSize, itemColor, itemBrand);
                    sellingItems.addItem(apparel);
                    break;

                // ... similar pattern for other cases ...
                case 2:
                    categoryName = "Collectible";
                    System.out.println("Enter name of Collectible (or 'back' to return to menu): ");
                    itemName = scanner.nextLine();
                    if (itemName.equalsIgnoreCase("back")) return;

                    System.out.println("Enter cost of Collectible (or 'back' to return to menu): ");
                    costInput = scanner.nextLine();
                    if (costInput.equalsIgnoreCase("back")) return;
                    itemCost = Double.parseDouble(costInput);

                    System.out.println("Enter image of Collectible (or 'back' to return to menu): ");
                    itemImage = scanner.nextLine();
                    if (itemImage.equalsIgnoreCase("back")) return;

                    System.out.println("Enter type of Collectible (or 'back' to return to menu): ");
                    String itemType = scanner.nextLine();
                    if (itemType.equalsIgnoreCase("back")) return;

                    System.out.println("Enter condition of Collectible (or 'back' to return to menu): ");
                    String itemCondition = scanner.nextLine();
                    if (itemCondition.equalsIgnoreCase("back")) return;

                    Collectible collectible = new Collectible(itemName, itemCost, currentUser,
                            itemImage, categoryName, itemType, itemCondition);
                    sellingItems.addItem(collectible);
                    break;

                case 3:
                    categoryName = "Electronic";
                    System.out.println("Enter name of Electronic (or 'back' to return to menu): ");
                    itemName = scanner.nextLine();
                    if (itemName.equalsIgnoreCase("back")) return;

                    System.out.println("Enter cost of Electronic (or 'back' to return to menu): ");
                    costInput = scanner.nextLine();
                    if (costInput.equalsIgnoreCase("back")) return;
                    itemCost = Double.parseDouble(costInput);

                    System.out.println("Enter image of Electronic (or 'back' to return to menu): ");
                    itemImage = scanner.nextLine();
                    if (itemImage.equalsIgnoreCase("back")) return;

                    System.out.println("Enter type of Electronic (or 'back' to return to menu): ");
                    itemType = scanner.nextLine();
                    if (itemType.equalsIgnoreCase("back")) return;

                    System.out.println("Enter year of Electronic (or 'back' to return to menu): ");
                    String yearInput = scanner.nextLine();
                    if (yearInput.equalsIgnoreCase("back")) return;
                    int itemYear = Integer.parseInt(yearInput);

                    Electronic electronic = new Electronic(itemName, itemCost, currentUser, itemImage,
                            categoryName, itemType, itemYear);
                    sellingItems.addItem(electronic);
                    break;

                case 4:
                    categoryName = "Home";
                    System.out.println("Enter name of Home (or 'back' to return to menu): ");
                    itemName = scanner.nextLine();
                    if (itemName.equalsIgnoreCase("back")) return;

                    System.out.println("Enter cost of Home (or 'back' to return to menu): ");
                    costInput = scanner.nextLine();
                    if (costInput.equalsIgnoreCase("back")) return;
                    itemCost = Double.parseDouble(costInput);

                    System.out.println("Enter image of Home (or 'back' to return to menu): ");
                    itemImage = scanner.nextLine();
                    if (itemImage.equalsIgnoreCase("back")) return;

                    System.out.println("Enter type of Home (or 'back' to return to menu): ");
                    itemType = scanner.nextLine();
                    if (itemType.equalsIgnoreCase("back")) return;

                    Home home = new Home(itemName, itemCost, currentUser, itemImage,
                            categoryName, itemType);
                    sellingItems.addItem(home);
                    break;

                case 5:
                    categoryName = "Vehicle";
                    System.out.println("Enter name of Vehicle (or 'back' to return to menu): ");
                    itemName = scanner.nextLine();
                    if (itemName.equalsIgnoreCase("back")) return;

                    System.out.println("Enter cost of Vehicle (or 'back' to return to menu): ");
                    costInput = scanner.nextLine();
                    if (costInput.equalsIgnoreCase("back")) return;
                    itemCost = Double.parseDouble(costInput);

                    System.out.println("Enter image of Vehicle (or 'back' to return to menu): ");
                    itemImage = scanner.nextLine();
                    if (itemImage.equalsIgnoreCase("back")) return;

                    System.out.println("Enter mileage of Vehicle (or 'back' to return to menu): ");
                    String mileageInput = scanner.nextLine();
                    if (mileageInput.equalsIgnoreCase("back")) return;
                    int itemMileage = Integer.parseInt(mileageInput);

                    System.out.println("Enter year of Vehicle (or 'back' to return to menu): ");
                    yearInput = scanner.nextLine();
                    if (yearInput.equalsIgnoreCase("back")) return;
                    itemYear = Integer.parseInt(yearInput);

                    System.out.println("Enter brand of Vehicle (or 'back' to return to menu): ");
                    itemBrand = scanner.nextLine();
                    if (itemBrand.equalsIgnoreCase("back")) return;

                    Vehicle vehicle = new Vehicle(itemName, itemCost, currentUser, itemImage,
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
            System.out.println("Choose a category (or type 'back' to return to menu): \n 1. Apparel\n 2. Collectible\n 3. Electronic\n 4. Home\n 5. Vehicle");
            String input = scanner.nextLine();
            
            if (input.equalsIgnoreCase("back")) {
                return;
            }
            
            int categoryChoice = Integer.parseInt(input);
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

            System.out.print("\nEnter the number of the item to purchase (or type 'back' to return to menu): ");
            input = scanner.nextLine();
            
            if (input.equalsIgnoreCase("back")) {
                return;
            }

            int itemIndex = Integer.parseInt(input) - 1;

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
                // Remove the redundant balance update since it's already handled in purchaseItem
                System.out.println("Purchase successful! Remaining Balance: $" + String.format("%.2f", currentUser.getBalance()));
                
                // Add rating prompt after successful purchase
                promptForSellerRating(selectedItem.getSoldBy());
            } else {
                System.out.println("Purchase failed.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Error during purchase: " + e.getMessage());
        }
    }

    private void promptForSellerRating(User seller) {
        System.out.println("\nWould you like to rate the seller? (Y/N)");
        String response = scanner.nextLine();
        if (response.equalsIgnoreCase("Y")) {
            System.out.println("Rate the seller from 1-5 (5 being the best):");
            try {
                int rating = Integer.parseInt(scanner.nextLine());
                if (seller.addSellerRating(rating, currentUser)) {
                    System.out.println("Rating submitted successfully!");
                    System.out.printf("Seller's current rating: %.1f (%d ratings)%n",
                            seller.getAverageSellerRating(),
                            seller.getNumberOfRatings());
                } else {
                    System.out.println("Rating must be between 1 and 5.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
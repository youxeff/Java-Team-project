package Network;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

import Service.Marketplace;
import model.items.*;
import model.users.MarketplaceUser;

/**
 * Per-client connection manager for the marketplace system.
 * Handles all marketplace functionality, session management, and input validation.
 * 
 * @author Youssef Abdelkader
 * @author Anthony Kim  
 * @author Caroline Murphy
 * @author Eric Yen
 * @author Isaac Yoon
 * @version April 20 2025
 */
class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private MarketplaceUser currentUser = null;
    private Marketplace marketplace;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.marketplace = new Marketplace();
    }

    @Override
    public void run() {
        try {
            out.println("Welcome to the marketplace!");
            boolean running = true;
            
            while (running) {
                if (currentUser == null) {
                    out.println("\n=== Marketplace System ===");
                    out.println("1. Register New User");
                    out.println("2. Login");
                    out.println("3. Exit");
                    out.print("Choose an option: ");
                    
                    String choice = in.readLine();
                    switch (choice) {
                        case "1":
                            registerUser();
                            break;
                        case "2":
                            loginUser();
                            break;
                        case "3":
                            running = false;
                            out.println("Goodbye!");
                            break;
                        default:
                            out.println("Invalid option. Please try again.");
                    }
                } else {
                    showUserMenu();
                }
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Failed to close socket: " + e.getMessage());
            }
        }
    }

    private void registerUser() throws IOException {
        out.println("\n=== User Registration ===");
        out.print("Enter First Name: ");
        String firstName = in.readLine();

        out.print("Enter Last Name: ");
        String lastName = in.readLine();

        out.print("Enter Username: ");
        String username = in.readLine();

        out.print("Enter Password: ");
        String password = in.readLine();

        try {
            if (MarketplaceUser.loadUser(username) != null) {
                out.println("Username already exists.");
                return;
            }
            MarketplaceUser newUser = new MarketplaceUser(firstName, lastName, username, password);
            marketplace.updateUserData(newUser);

            out.println("Registration successful!");

        } catch (Exception e) {
            out.println("Error during registration: " + e.getMessage());
        }
    }

    private void loginUser() throws IOException {
        out.println("\n=== User Login ===");
        out.print("Enter Username: ");
        String username = in.readLine();

        out.print("Enter Password: ");
        String password = in.readLine();

        try {
            currentUser = MarketplaceUser.loadUser(username);
            if (currentUser != null && currentUser.verifyPassword(password)) {
                out.println("Login successful!");
            } else {
                out.println("Login failed. Invalid credentials.");
                currentUser = null;
            }
        } catch (Exception e) {
            out.println("Error during login: " + e.getMessage());
        }
    }

    private void showUserMenu() throws IOException {
        out.println("\n=== User Menu ===");
        out.println("1. View Profile");
        out.println("2. Update Balance");
        out.println("3. Choose Buy or Sell");
        out.println("4. Send a Message");
        out.println("5. View Messages");
        out.println("6. Logout");
        out.print("Choose an option: ");

        String choice = in.readLine();
        System.out.println("what is this why isn't it wokring");
        switch (choice) {
            case "1":
                displayUserProfile();
                break;
            case "2":
                updateBalance();
                break;
            case "3":
                buyOrSell();
                break;
            case "4":
                sendMessage();
                break;
            case "5":
                viewMessages();
                break;
            case "6":
                currentUser = null;
                out.println("Logged out successfully.");
                break;
            default:
                out.println("Invalid option. Please try again.");
        }
    }

    private void displayUserProfile() {
        out.println("\n=== User Profile ===");
        out.println("First Name: " + currentUser.getFirstName());
        out.println("Last Name: " + currentUser.getLastName());
        out.println("Username: " + currentUser.getUserName());
        out.println("Balance: $" + String.format("%.2f", currentUser.getBalance()));
    }

    private void updateBalance() throws IOException {
        out.print("Enter new balance amount: $");
        String balanceInput = in.readLine();
        
        try {
            double newBalance = Double.parseDouble(balanceInput);
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
            out.println("Balance updated successfully!");
        } catch (NumberFormatException e) {
            out.println("Please enter a valid number.");
        } catch (IOException e) {
            out.println("Error updating balance: " + e.getMessage());
        }
    }

    private void buyOrSell() throws IOException {
        boolean buyOrSellMenu = true;
        while (buyOrSellMenu) {
            out.println("1. Sell");
            out.println("2. Buy");
            out.println("3. Exit");
            out.print("Choose an option: ");
            
            String choice = in.readLine();
            switch (choice) {
                case "1":
                    sellItem();
                    break;
                case "2":
                    buyItem();
                    break;
                case "3":
                    buyOrSellMenu = false;
                    break;
                default:
                    out.println("Invalid option. Please try again.");
            }
        }
    }

    private void sellItem() throws IOException {
        out.println("\n=== Add Item for Sale ===");
        out.println("Choose a category:");
        out.println("1. Apparel");
        out.println("2. Collectible");
        out.println("3. Electronic");
        out.println("4. Home");
        out.println("5. Vehicle");
        out.print("Enter category number: ");
        
        int category;
        try {
            category = Integer.parseInt(in.readLine());
        } catch (NumberFormatException e) {
            out.println("Please enter a valid number.");
            return;
        }

        Marketplace sellingItems = new Marketplace() {
            @Override
            public synchronized ArrayList<model.users.User> loadAllUsers() {
                return new ArrayList<>();
            }
        };

        switch (category) {
            case 1:
                String categoryName = "Apparel";
                out.println("Enter name of Apparel: ");
                String itemName = in.readLine();
                out.println("Enter cost of Apparel: ");
                double itemCost = Double.parseDouble(in.readLine());
                out.println("Enter image of Apparel: ");
                String itemImage = in.readLine();
                out.println("Enter size of Apparel: ");
                String itemSize = in.readLine();
                out.println("Enter color of Apparel: ");
                String itemColor = in.readLine();
                out.println("Enter brand of Apparel: ");
                String itemBrand = in.readLine();

                Apparel apparel = new Apparel(itemName, itemCost, currentUser, itemImage,
                        categoryName, itemSize, itemColor, itemBrand);
                sellingItems.addItem(apparel);
                break;
                
            case 2:
                categoryName = "Collectible";
                out.println("Enter name of Collectible: ");
                itemName = in.readLine();
                out.println("Enter cost of Collectible: ");
                itemCost = Double.parseDouble(in.readLine());
                out.println("Enter image of Collectible: ");
                itemImage = in.readLine();
                out.println("Enter type of Collectible: ");
                String itemType = in.readLine();
                out.println("Enter condition of Collectible: ");
                String itemCondition = in.readLine();

                Collectible collectible = new Collectible(itemName, itemCost, currentUser,
                        itemImage, categoryName, itemType, itemCondition);
                sellingItems.addItem(collectible);
                break;
                
            case 3:
                categoryName = "Electronic";
                out.println("Enter name of Electronic: ");
                itemName = in.readLine();
                out.println("Enter cost of Electronic: ");
                itemCost = Double.parseDouble(in.readLine());
                out.println("Enter image of Electronic: ");
                itemImage = in.readLine();
                out.println("Enter type of Electronic: ");
                itemType = in.readLine();
                out.println("Enter year of Electronic: ");
                int itemYear = Integer.parseInt(in.readLine());

                Electronic electronic = new Electronic(itemName, itemCost, currentUser, itemImage,
                        categoryName, itemType, itemYear);
                sellingItems.addItem(electronic);
                break;
                
            case 4:
                categoryName = "Home";
                out.println("Enter name of Home: ");
                itemName = in.readLine();
                out.println("Enter cost of Home: ");
                itemCost = Double.parseDouble(in.readLine());
                out.println("Enter image of Home: ");
                itemImage = in.readLine();
                out.println("Enter type of Home: ");
                itemType = in.readLine();

                Home home = new Home(itemName, itemCost, currentUser, itemImage,
                        categoryName, itemType);
                sellingItems.addItem(home);
                break;
                
            case 5:
                categoryName = "Vehicle";
                out.println("Enter name of Vehicle: ");
                itemName = in.readLine();
                out.println("Enter cost of Vehicle: ");
                itemCost = Double.parseDouble(in.readLine());
                out.println("Enter image of Vehicle: ");
                itemImage = in.readLine();
                out.println("Enter mileage of Vehicle: ");
                int itemMileage = Integer.parseInt(in.readLine());
                out.println("Enter year of Vehicle: ");
                itemYear = Integer.parseInt(in.readLine());
                out.println("Enter brand of Vehicle: ");
                itemBrand = in.readLine();

                Vehicle vehicle = new Vehicle(itemName, itemCost, currentUser, itemImage,
                        categoryName, itemMileage, itemYear, itemBrand);
                sellingItems.addItem(vehicle);
                break;
                
            default:
                out.println("Invalid category.");
                return;
        }
        out.println("Item added successfully!");
    }

    private void buyItem() throws IOException {
        try {
            out.println("Choose a category: \n 1. Apparel\n 2. Collectible\n 3. Electronic\n 4. Home\n 5. Vehicle");
            int categoryChoice = Integer.parseInt(in.readLine());

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
                    out.println("Invalid category.");
                    return;
            }

            ArrayList<Item> categoryItems = marketplace.searchByCategory(category);
            if (categoryItems.isEmpty()) {
                out.println("No items found in this category.");
                return;
            }

            out.println("\nAvailable Items:");
            for (int i = 0; i < categoryItems.size(); i++) {
                Item item = categoryItems.get(i);
                if (item.isAvailable()) {
                    out.println(String.format(
                            "[%d] %s - $%.2f (Seller: %s)",
                            i + 1,
                            item.getName(),
                            item.getCost(),
                            item.getSoldBy().getUserName()));
                }
            }

            out.println("\nEnter the number of the item to purchase: ");
            int itemIndex = Integer.parseInt(in.readLine()) - 1;

            if (itemIndex < 0 || itemIndex >= categoryItems.size()) {
                out.println("Invalid item number.");
                return;
            }

            Item selectedItem = categoryItems.get(itemIndex);

            if (selectedItem.getCost() > currentUser.getBalance()) {
                out.println("Insufficient balance to complete the purchase.");
                return;
            }

            boolean success = marketplace.purchaseItem(selectedItem, currentUser);
            if (success) {
                currentUser.setBalance(currentUser.getBalance() - selectedItem.getCost());
                out.println("Purchase successful! Remaining Balance: $" + 
                String.format("%.2f", currentUser.getBalance()));
            } else {
                out.println("Purchase failed.");
            }

        } catch (NumberFormatException e) {
            out.println("Please enter a valid number.");
        } catch (Exception e) {
            out.println("Error during purchase: " + e.getMessage());
        }
    }

    private void sendMessage() throws IOException {
        out.print("Who do you want to message? ");
        String recipientUsername = in.readLine();
        
        if (!MarketplaceUser.userExists(recipientUsername)) {
            out.println("User \"" + recipientUsername + "\" does not exist.");
            return;
        }
        
        out.print("What is your message? ");
        String message = in.readLine();
        
        currentUser.sendMessageTo(recipientUsername, message);
        out.println("Message sent successfully!");
    }

    private void viewMessages() {
        System.out.println("help");
        ArrayList<String> messages = currentUser.viewMessages();
        System.out.println("help");
        
        if (messages.isEmpty()) {
            out.println("You have no messages.");
            return;
        }
        
        out.println("\n=== Your Messages ===");
        for (String message : messages) {
            out.println(message);
        }
    }
}
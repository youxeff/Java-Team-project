package Network;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

import Service.Marketplace;
import model.items.*;
import model.users.MarketplaceUser;

/**
 * Handles individual client connections to the marketplace server.
 * Manages user authentication, messaging, and marketplace operations for each client.
 * Implements Runnable to handle multiple clients concurrently.
 */
class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private MarketplaceUser currentUser = null;
    private Marketplace marketplace;

    /**
     * Constructs a new ClientHandler for a connected client.
     * Initializes I/O streams and marketplace instance.
     *
     * @param clientSocket the socket connection to the client
     * @throws IOException if there's an error setting up the I/O streams
     */
    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.marketplace = new Marketplace();
    }

    /**
     * Main client handling loop. Processes client requests and manages user sessions.
     * Implements the Runnable interface's run method.
     */
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
                    out.println("Choose an option: ");
                    
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

    /**
     * Handles user registration process.
     * Collects user information and creates new account.
     *
     * @throws IOException if there's an error reading client input
     */
    private void registerUser() throws IOException {
        out.println("\n=== User Registration ===");
        out.print("Enter First Name (or 'back' to return): ");
        String firstName = in.readLine();
        
        if (firstName.equalsIgnoreCase("back")) {
            return;
        }

        out.print("Enter Last Name (or 'back' to return): ");
        String lastName = in.readLine();
        
        if (lastName.equalsIgnoreCase("back")) {
            return;
        }

        out.print("Enter Username (or 'back' to return): ");
        String username = in.readLine();
        
        if (username.equalsIgnoreCase("back")) {
            return;
        }

        out.print("Enter Password (or 'back' to return): ");
        String password = in.readLine();
        
        if (password.equalsIgnoreCase("back")) {
            return;
        }

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

    /**
     * Handles user login process.
     * Verifies credentials and initializes user session.
     *
     * @throws IOException if there's an error reading client input
     */
    private void loginUser() throws IOException {
        out.println("\n=== User Login ===");
        out.print("Enter Username (or 'back' to return): ");
        String username = in.readLine();

        if (username.equalsIgnoreCase("back")) {
            return;
        }

        out.print("Enter Password (or 'back' to return): ");
        String password = in.readLine();

        if (password.equalsIgnoreCase("back")) {
            return;
        }

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

    /**
     * Displays and handles user menu options after login.
     * Routes to appropriate functionality based on user choice.
     *
     * @throws IOException if there's an error reading client input
     */
    private void showUserMenu() throws IOException {
        out.println("\n=== User Menu ===");
        out.println("1. View Profile");
        out.println("2. Update Balance");
        out.println("3. Choose Buy or Sell");
        out.println("4. Send a Message");
        out.println("5. View Messages");
        out.println("6. Logout");
        out.println("Choose an option: ");

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

    /**
     * Displays user profile information.
     * Shows name, username, and account balance.
     */
    private void displayUserProfile() {
        out.println("\n=== User Profile ===");
        out.println("First Name: " + currentUser.getFirstName());
        out.println("Last Name: " + currentUser.getLastName());
        out.println("Username: " + currentUser.getUserName());
        out.println("Balance: $" + String.format("%.2f", currentUser.getBalance()));
    }

    /**
     * Updates user's account balance.
     * Validates and processes balance changes.
     *
     * @throws IOException if there's an error updating balance data
     */
    private void updateBalance() throws IOException {
        out.print("Enter new balance amount (or 'back' to return): $");
        String balanceInput = in.readLine();
        
        if (balanceInput.equalsIgnoreCase("back")) {
            return;
        }
        
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

    /**
     * Handles buying and selling menu options.
     * Routes to appropriate transaction functionality.
     *
     * @throws IOException if there's an error processing the transaction
     */
    private void buyOrSell() throws IOException {
        boolean buyOrSellMenu = true;
        while (buyOrSellMenu) {
            out.println("1. Sell");
            out.println("2. Buy");
            out.println("3. Exit");
            out.println("Choose an option: ");
            
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

    /**
     * Handles the process of listing an item for sale.
     * Collects item details based on category.
     *
     * @throws IOException if there's an error creating the listing
     */
    private void sellItem() throws IOException {
        out.println("\n=== Add Item for Sale ===");
        out.print("Choose a category (or 'back' to return):\n1. Apparel\n2. Collectible\n3. Electronic\n4. Home\n5. Vehicle\nEnter category number: ");
        
        String input = in.readLine();
        if (input.equalsIgnoreCase("back")) {
            return;
        }

        int category;
        try {
            category = Integer.parseInt(input);
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

    /**
     * Handles the process of purchasing an item.
     * Shows available items and processes transaction.
     *
     * @throws IOException if there's an error processing the purchase
     */
    private void buyItem() throws IOException {
        try {
            out.print("Choose a category (or 'back' to return): \n 1. Apparel\n 2. Collectible\n 3. Electronic\n 4. Home\n 5. Vehicle\nEnter choice: ");
            String input = in.readLine();
            
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
                    out.println(String.format("[%d] %s - $%.2f (Seller: %s)", i + 1,
                            item.getName(), item.getCost(), item.getSoldBy().getUserName()));
                }
            }

            out.print("\nEnter the number of the item to purchase (or 'back' to return): ");
            input = in.readLine();
            
            if (input.equalsIgnoreCase("back")) {
                return;
            }

            int itemIndex = Integer.parseInt(input) - 1;

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
                out.println("Purchase successful! Remaining Balance: $" + String.format("%.2f", currentUser.getBalance()));
            } else {
                out.println("Purchase failed.");
            }

        } catch (NumberFormatException e) {
            out.println("Please enter a valid number.");
        } catch (Exception e) {
            out.println("Error during purchase: " + e.getMessage());
        }
    }

    /**
     * Handles sending messages between users.
     * Validates recipient and delivers message.
     *
     * @throws IOException if there's an error sending the message
     */
    private void sendMessage() throws IOException {
        out.print("Who do you want to message? (or 'back' to return): ");
        String recipientUsername = in.readLine();
        
        if (recipientUsername.equalsIgnoreCase("back")) {
            return;
        }
        
        if (!MarketplaceUser.userExists(recipientUsername)) {
            out.println("User \"" + recipientUsername + "\" does not exist.");
            return;
        }
        
        out.print("What is your message? (or 'back' to return): ");
        String message = in.readLine();
        
        if (message.equalsIgnoreCase("back")) {
            return;
        }
        
        currentUser.sendMessageTo(recipientUsername, message);
        out.println("Message sent successfully!");
    }

    /**
     * Displays all messages received by the current user.
     * Shows message content with sender information.
     */
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
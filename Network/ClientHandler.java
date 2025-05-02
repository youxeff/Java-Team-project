package Network;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

import Service.Marketplace;
import model.items.*;
import model.users.MarketplaceUser;
import model.users.User;

/**
 * Per-client connection manager for the marketplace system.
 * Handles all marketplace functionality, session management, and input
 * validation.
 * Each instance manages a single client connection in its own thread.
 * 
 * @author Youssef Abdelkader
 * @author Anthony Kim
 * @author Caroline Murphy
 * @author Eric Yen
 * @author Isaac Yoon
 * @version April 20 2025
 */
public class ClientHandler implements Runnable, IClientHandler {
    /** Socket for the client connection */
    private Socket clientSocket;
    /** Input stream for receiving client messages */
    private BufferedReader in;
    /** Output stream for sending messages to client */
    private PrintWriter out;
    /** Currently logged in user, null if no user is logged in */
    private MarketplaceUser currentUser = null;
    /** Marketplace instance for handling business logic */
    private Marketplace marketplace;

    /**
     * Creates a new client handler for a connected client.
     * Initializes the communication streams and marketplace instance.
     *
     * @param clientSocket the socket connection to the client
     * @throws IOException if there's an error setting up the streams
     */
    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.marketplace = new Marketplace();
    }

    /**
     * Main processing loop for the client connection.
     * Handles authentication and routes client requests to appropriate handlers.
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
     * {@inheritDoc}
     * Prompts for and processes user registration information.
     * Validates input and creates new user account if valid.
     */
    @Override
    public void registerUser() throws IOException {
        out.println("\n=== User Registration ===");
        out.println("Enter First Name: ");
        String firstName = in.readLine();

        out.println("Enter Last Name: ");
        String lastName = in.readLine();

        out.println("Enter Username: ");
        String username = in.readLine();

        out.println("Enter Password: ");
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

    /**
     * {@inheritDoc}
     * Authenticates user credentials and establishes user session if valid.
     */
    @Override
    public void loginUser() throws IOException {
        out.println("\n=== User Login ===");
        out.println("Enter Username: ");
        String username = in.readLine();

        out.println("Enter Password: ");
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

    /**
     * {@inheritDoc}
     * Displays main menu for logged-in users and processes their choices.
     */
    @Override
    public void showUserMenu() throws IOException {
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
     * {@inheritDoc}
     * Sends current user's profile information to the client.
     */
    @Override
    public void displayUserProfile() {
        out.println("\n=== User Profile ===");
        out.println("First Name: " + currentUser.getFirstName());
        out.println("Last Name: " + currentUser.getLastName());
        out.println("Username: " + currentUser.getUserName());
        out.println("Balance: $" + String.format("%.2f", currentUser.getBalance()));
        out.printf("Seller Rating: %.1f (%d ratings)%n",
                currentUser.getAverageSellerRating(),
                currentUser.getNumberOfRatings());
    }

    /**
     * Updates the user's balance in both memory and persistent storage.
     * Validates input and ensures proper synchronization of updates.
     *
     * @throws IOException if there's an error updating the balance in storage
     */
    private void updateBalance() throws IOException {
        out.println("Enter new balance amount: $");
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
                    String updatedLine = String.format(
                            "%s,%s,%s,%s,%.2f",
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
     * {@inheritDoc}
     * Manages the buy/sell menu loop and routes to appropriate handlers.
     */
    @Override
    public void buyOrSell() throws IOException {
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
     * {@inheritDoc}
     * Handles the complete process of listing a new item for sale.
     * Includes category selection and item-specific attribute collection.
     */
    @Override
    public void sellItem() throws IOException {
        JFrame frame = new JFrame("List New Item");
        frame.setSize(500, 600);
        frame.setLayout(new BorderLayout());
    
        // Common fields panel
        JPanel commonPanel = new JPanel(new GridLayout(3, 2));
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JComboBox<String> categoryCombo = new JComboBox<>(
            new String[]{"Apparel", "Collectible", "Electronic", "Home", "Vehicle"});
    
        commonPanel.add(new JLabel("Item Name:"));
        commonPanel.add(nameField);
        commonPanel.add(new JLabel("Price:"));
        commonPanel.add(priceField);
        commonPanel.add(new JLabel("Category:"));
        commonPanel.add(categoryCombo);
    
        // Category panels
        JPanel cardPanel = new JPanel(new CardLayout());
        
        // Apparel
        JPanel apparelPanel = new JPanel(new GridLayout(3, 2));
        JTextField sizeField = new JTextField();
        JTextField colorField = new JTextField();
        JTextField brandField = new JTextField();
        apparelPanel.add(new JLabel("Size:"));
        apparelPanel.add(sizeField);
        apparelPanel.add(new JLabel("Color:"));
        apparelPanel.add(colorField);
        apparelPanel.add(new JLabel("Brand:"));
        apparelPanel.add(brandField);
        
        // Collectible
        JPanel collectiblePanel = new JPanel(new GridLayout(2, 2));
        JTextField cTypeField = new JTextField();
        JTextField conditionField = new JTextField();
        collectiblePanel.add(new JLabel("Type:"));
        collectiblePanel.add(cTypeField);
        collectiblePanel.add(new JLabel("Condition:"));
        collectiblePanel.add(conditionField);
        
        // Electronic
        JPanel electronicPanel = new JPanel(new GridLayout(2, 2));
        JTextField eTypeField = new JTextField();
        JSpinner yearSpinner = new JSpinner(
            new SpinnerNumberModel(2023, 1900, LocalDateTime.now().getYear(), 1));
        electronicPanel.add(new JLabel("Type:"));
        electronicPanel.add(eTypeField);
        electronicPanel.add(new JLabel("Year:"));
        electronicPanel.add(yearSpinner);
        
        // Home
        JPanel homePanel = new JPanel(new GridLayout(1, 2));
        JTextField hTypeField = new JTextField();
        homePanel.add(new JLabel("Type:"));
        homePanel.add(hTypeField);
        
        // Vehicle
        JPanel vehiclePanel = new JPanel(new GridLayout(3, 2));
        JSpinner mileageSpinner = new JSpinner(
            new SpinnerNumberModel(0, 0, 1000000, 1000));
        JSpinner vYearSpinner = new JSpinner(
            new SpinnerNumberModel(2023, 1900, LocalDateTime.now().getYear(), 1));
        JTextField vBrandField = new JTextField();
        vehiclePanel.add(new JLabel("Mileage:"));
        vehiclePanel.add(mileageSpinner);
        vehiclePanel.add(new JLabel("Year:"));
        vehiclePanel.add(vYearSpinner);
        vehiclePanel.add(new JLabel("Brand:"));
        vehiclePanel.add(vBrandField);
        
        // Add all panels to card layout
        cardPanel.add(apparelPanel, "Apparel");
        cardPanel.add(collectiblePanel, "Collectible");
        cardPanel.add(electronicPanel, "Electronic");
        cardPanel.add(homePanel, "Home");
        cardPanel.add(vehiclePanel, "Vehicle");
    
        // Show appropriate panel whenever category changes
        categoryCombo.addActionListener(e -> {
            CardLayout cl = (CardLayout)(cardPanel.getLayout());
            cl.show(cardPanel, (String)categoryCombo.getSelectedItem());
        });
    
        // Submit button
        JButton submitButton = new JButton("List Item");
        submitButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter an item name", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                double price = Double.parseDouble(priceField.getText());
                if (price <= 0) {
                    JOptionPane.showMessageDialog(frame, "Price must be greater than 0", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String category = (String)categoryCombo.getSelectedItem();
                String imagePath = ""; // Empty since we removed image functionality
    
                Item newItem;
                switch(category) {
                    case "Apparel":
                        newItem = new Apparel(name, price, currentUser, imagePath, category, 
                            sizeField.getText(), colorField.getText(), brandField.getText());
                        break;
                    case "Collectible":
                        newItem = new Collectible(name, price, currentUser, imagePath, category,
                            cTypeField.getText(), conditionField.getText());
                        break;
                    case "Electronic":
                        newItem = new Electronic(name, price, currentUser, imagePath, category,
                            eTypeField.getText(), (int)yearSpinner.getValue());
                        break;
                    case "Home":
                        newItem = new Home(name, price, currentUser, imagePath, category,
                            hTypeField.getText());
                        break;
                    case "Vehicle":
                        newItem = new Vehicle(name, price, currentUser, imagePath, category,
                            (int)mileageSpinner.getValue(), (int)vYearSpinner.getValue(), 
                            vBrandField.getText());
                        break;
                    default:
                        JOptionPane.showMessageDialog(frame, "Invalid category", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                }
    
                marketplace.addItem(newItem);
                JOptionPane.showMessageDialog(frame, "Item listed successfully!");
                frame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid price", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        frame.add(commonPanel, BorderLayout.NORTH);
        frame.add(cardPanel, BorderLayout.CENTER);
        frame.add(submitButton, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    /**
     * {@inheritDoc}
     * Manages the item purchase workflow including:
     * - Category browsing
     * - Item selection
     * - Balance verification
     * - Purchase transaction
     * - Optional seller rating
     */
    @Override
    public void buyItem() throws IOException {
        out.println("Choose a category:");
        out.println(" 1. Apparel");
        out.println(" 2. Collectible");
        out.println(" 3. Electronic");
        out.println(" 4. Home");
        out.println(" 5. Vehicle");

        int categoryChoice = getValidatedInteger("Enter category number (1-5): ", 1, 5);
        if (categoryChoice == -1)
            return;

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
                        i + 1, item.getName(), item.getCost(), item.getSoldBy().getUserName()));
            }
        }

        int itemIndex = getValidatedInteger(
                "\nEnter the number of the item to purchase: ",
                1,
                categoryItems.size()) - 1;
        if (itemIndex == -2)
            return; // -2 because we subtracted 1 from -1

        Item selectedItem = categoryItems.get(itemIndex);

        if (selectedItem.getCost() > currentUser.getBalance()) {
            out.println("Insufficient balance to complete the purchase.");
            return;
        }

        boolean success = marketplace.purchaseItem(selectedItem, currentUser);
        if (success) {
            currentUser.setBalance(currentUser.getBalance() - selectedItem.getCost());
            out.println("Purchase successful! Remaining Balance: $" + String.format("%.2f", currentUser.getBalance()));
            promptForSellerRating(selectedItem.getSoldBy());
        } else {
            out.println("Purchase failed.");
        }
    }

    /**
     * Handles the sending of messages between users.
     * Validates recipient existence and delivers the message.
     *
     * @throws IOException if there's an error in message transmission
     */
    private void sendMessage() throws IOException {
        out.println("Who do you want to message?");
        String recipientUsername = in.readLine();

        if (!MarketplaceUser.userExists(recipientUsername)) {
            out.println("User \"" + recipientUsername + "\" does not exist.");
            return;
        }

        out.println("What is your message?");
        String message = in.readLine();

        currentUser.sendMessageTo(recipientUsername, message);
        out.println("Message sent successfully!");
    }

    /**
     * {@inheritDoc}
     * Retrieves and displays all messages for the current user.
     */
    @Override
    public void viewMessages() {
        ArrayList<String> messages = currentUser.viewMessages();

        if (messages.isEmpty()) {
            out.println("You have no messages.");
            return;
        }

        out.println("\n=== Your Messages ===");
        for (String message : messages) {
            out.println(message);
        }
    }

    /**
     * {@inheritDoc}
     * Handles the seller rating process after a successful purchase.
     */
    @Override
    public void promptForSellerRating(User seller) throws IOException {
        out.println("\nWould you like to rate the seller? (Y/N)");
        String response = in.readLine();
        if (response.equalsIgnoreCase("Y")) {
            int rating = getValidatedInteger("Rate the seller (1-5, 5 being the best): ", 1, 5);
            if (rating == -1)
                return;

            if (seller.addSellerRating(rating, currentUser)) {
                out.println("Rating submitted successfully!");
                out.printf("Seller's current rating: %.1f (%d ratings)%n",
                        seller.getAverageSellerRating(),
                        seller.getNumberOfRatings());
            }
        }
    }

    /**
     * Validates and parses an integer input with retry logic.
     * Keeps prompting until valid input is received or user cancels.
     *
     * @param prompt The prompt to display to the user
     * @param min    Minimum allowed value (inclusive)
     * @param max    Maximum allowed value (inclusive)
     * @return The validated integer, or -1 if user cancels
     * @throws IOException if there's an error reading input
     */
    private int getValidatedInteger(String prompt, int min, int max) throws IOException {
        while (true) {
            out.println(prompt);
            String input = in.readLine();

            if (input.equalsIgnoreCase("back")) {
                return -1;
            }

            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                } else {
                    out.println("Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                out.println("Please enter a valid number.");
            }
        }
    }
}

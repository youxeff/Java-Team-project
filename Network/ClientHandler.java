package Network;

import java.io.*;
import  java.net.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
public class ClientHandler extends JComponent implements Runnable, IClientHandler {
    /**
     * Socket for the client connection
     */
    private Socket clientSocket;
    /**
     * Input stream for receiving client messages
     */
    private BufferedReader in;
    /**
     * Output stream for sending messages to client
     */
     PrintWriter out;
    /**
     * Currently logged in user, null if no user is logged in
     */
    private MarketplaceUser currentUser = null;
    /**
     * Marketplace instance for handling business logic
     */
    private Marketplace marketplace;


    // Main GUI components
    private JFrame mainFrame;
    private JPanel mainPanel;
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // User info components
    private JLabel usernameLabel;
    private JLabel balanceLabel;
    private JLabel ratingLabel;

    // Profile panel components
    private JPanel profilePanel;
    private JLabel firstNameLabel;
    private JLabel lastNameLabel;
    private JLabel profileUsernameLabel;
    private JLabel profileBalanceLabel;
    private JLabel sellerRatingLabel;
    private JLabel numRatingsLabel;
    private JTextField newBalanceField;
    private JButton updateBalanceButton;

    // Transaction history panel components
    private JPanel transactionHistoryPanel;
    private JTable purchasesTable;
    private JTable salesTable;
    private DefaultTableModel purchasesTableModel;
    private DefaultTableModel salesTableModel;

    // GUI Components for Authentication
    private JPanel cards;
    private CardLayout authCardLayout;
    private JFrame authFrame;

    private JTextField regFirstNameField;
    private JTextField regLastNameField;
    private JTextField regUsernameField;
    private JPasswordField regPasswordField;

    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;

    // Message GUI components
    private JFrame messageComposeFrame;
    private JTextField recipientField;
    private JTextArea messageArea;
    private JPanel messageInboxPanel;
    private DefaultListModel<String> messageListModel;
    private JList<String> messageList;
    private JTextArea messageViewArea;

    // Rating GUI components
    private User sellerToRate;

    private JTable itemsTable;
    private JPanel itemsPanel;

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
            boolean running = true;
            while (running) {
                if (currentUser == null) {
                    SwingUtilities.invokeLater(() -> createAuthGUI());

                    // Wait for auth GUI to complete
                    while (currentUser == null && authFrame != null && authFrame.isVisible()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (currentUser == null && (authFrame == null || !authFrame.isVisible())) {
                        running = false;
                    }
                } else {
                    SwingUtilities.invokeLater(() -> createAndShowGUI());

                    // Wait for main GUI to close
                    while (currentUser != null && mainFrame != null && mainFrame.isVisible()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Failed to close socket: " + e.getMessage());
            }
        }
    }

    public void createAuthGUI() {
        authFrame = new JFrame("Marketplace Authentication");
        authFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        authFrame.setSize(600, 400);

        authCardLayout = new CardLayout();
        cards = new JPanel(authCardLayout);

        cards.add(welcomePanel(), "Welcome");
        cards.add(loginPanel(), "Login");
        cards.add(registerPanel(), "Register");

        authFrame.add(cards, BorderLayout.CENTER);
        authFrame.setVisible(true);
    }

    private JPanel welcomePanel() {
        JPanel welcomePanel = new JPanel(new BorderLayout(20, 20));
        welcomePanel.setBackground(new Color(240, 240, 240));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Title
        JLabel titleLabel = new JLabel("Welcome to Marketplace", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(51, 51, 51));

        // Subtitle
        JLabel subtitleLabel = new JLabel("Buy and sell items in our community", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(102, 102, 102));

        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JButton loginButton = createStyledButton("Login", new Color(70, 130, 180));
        JButton registerButton = createStyledButton("Register New User", new Color(60, 179, 113));
        JButton exitButton = createStyledButton("Exit", new Color(128, 128, 128));

        loginButton.addActionListener(e -> authCardLayout.show(cards, "Login"));
        registerButton.addActionListener(e -> authCardLayout.show(cards, "Register"));
        exitButton.addActionListener(e -> authFrame.dispose());

        buttonsPanel.add(loginButton);
        buttonsPanel.add(registerButton);
        buttonsPanel.add(exitButton);

        welcomePanel.add(headerPanel, BorderLayout.NORTH);
        welcomePanel.add(buttonsPanel, BorderLayout.CENTER);

        return welcomePanel;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        return button;
    }

    private JPanel registerPanel() {
        JPanel registerPanel = new JPanel(new BorderLayout());
        JPanel registerGrid = new JPanel(new GridLayout(5, 2, 5, 5));

        regFirstNameField = new JTextField(10);
        regLastNameField = new JTextField(10);
        regUsernameField = new JTextField(10);
        regPasswordField = new JPasswordField(10);

        registerGrid.add(new JLabel("First Name:"));
        registerGrid.add(regFirstNameField);
        registerGrid.add(new JLabel("Last Name:"));
        registerGrid.add(regLastNameField);
        registerGrid.add(new JLabel("Username:"));
        registerGrid.add(regUsernameField);
        registerGrid.add(new JLabel("Password:"));
        registerGrid.add(regPasswordField);

        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");

        registerBtn.addActionListener(e -> register());
        backBtn.addActionListener(e -> authCardLayout.show(cards, "Welcome"));

        registerGrid.add(registerBtn);
        registerGrid.add(backBtn);

        registerPanel.add(registerGrid, BorderLayout.CENTER);
        return registerPanel;
    }

    private JPanel loginPanel() {
        JPanel loginPanel = new JPanel(new BorderLayout());
        JPanel loginGrid = new JPanel(new GridLayout(3, 2, 5, 5));

        loginUsernameField = new JTextField(10);
        loginPasswordField = new JPasswordField(10);

        loginGrid.add(new JLabel("Username:"));
        loginGrid.add(loginUsernameField);
        loginGrid.add(new JLabel("Password:"));
        loginGrid.add(loginPasswordField);

        JButton loginBtn = new JButton("Login");
        JButton backBtn = new JButton("Back");

        loginBtn.addActionListener(e -> login());
        backBtn.addActionListener(e -> authCardLayout.show(cards, "Welcome"));

        loginGrid.add(loginBtn);
        loginGrid.add(backBtn);

        loginPanel.add(loginGrid, BorderLayout.CENTER);
        return loginPanel;
    }

    private void register() {
        String first = regFirstNameField.getText();
        String last = regLastNameField.getText();
        String username = regUsernameField.getText();
        String password = new String(regPasswordField.getPassword());

        if (first.isEmpty() || last.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(authFrame, "All fields are required.");
            return;
        }

        try {
            if (MarketplaceUser.loadUser(username) != null) {
                JOptionPane.showMessageDialog(authFrame, "Username already exists.");
                return;
            }

            MarketplaceUser newUser = new MarketplaceUser(first, last, username, password);
            marketplace.updateUserData(newUser);
            JOptionPane.showMessageDialog(authFrame, "Registration successful! Please log in.");
            authCardLayout.show(cards, "Login");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(authFrame, "Error during registration: " + e.getMessage());
        }
    }

    private void login() {
        String username = loginUsernameField.getText();
        String password = new String(loginPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(authFrame, "Please enter both username and password.");
            return;
        }

        try {
            MarketplaceUser user = MarketplaceUser.loadUser(username);
            if (user != null && user.verifyPassword(password)) {
                currentUser = user;
                JOptionPane.showMessageDialog(authFrame, "Login successful! Welcome " + currentUser.getFirstName());
                authFrame.dispose();
                SwingUtilities.invokeLater(() -> {
                    createAndShowGUI();
                    loadUserTransactions(); // Load transaction history
                });
            } else {
                JOptionPane.showMessageDialog(authFrame, "Invalid username or password.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(authFrame, "Error during login: " + e.getMessage());
        }
    }

    private void loadUserTransactions() {
        // Clear existing data first
        purchasesTableModel.setRowCount(0);
        salesTableModel.setRowCount(0);

        ArrayList<String[]> transactions = marketplace.loadTransactions(currentUser.getUserName());
        for (String[] transaction : transactions) {
            if (transaction.length >= 7) {
                String buyerUsername = transaction[0];
                String itemName = transaction[2];
                String sellerUsername = transaction[3];
                String date = transaction[4];
                String price = "$" + transaction[5];
                String category = transaction[6];

                // Add to purchases table if current user is buyer
                if (buyerUsername.equals(currentUser.getUserName())) {
                    purchasesTableModel.addRow(new Object[]{
                            date,
                            itemName,
                            category,
                            sellerUsername,
                            price
                    });
                }

                // Add to sales table if current user is seller
                if (sellerUsername.equals(currentUser.getUserName())) {
                    salesTableModel.addRow(new Object[]{
                            date,
                            itemName,
                            category,
                            buyerUsername,
                            price
                    });
                }
            }
        }
    }

    private void createAndShowGUI() {
        mainFrame = new JFrame("Marketplace System");
        mainFrame.setSize(900, 600);
        mainFrame.setMinimumSize(new Dimension(800, 500));
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout());

        // Create sidebar for navigation
        createSidebar();

        // Create content panel with card layout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Create content panels
        createProfilePanel();
        createMessageInboxPanel();
        createTransactionHistoryPanel();

        contentPanel.add(profilePanel, "PROFILE");
        contentPanel.add(createBuyPanel(), "BUY");
        contentPanel.add(createSellPanel(), "SELL");
        contentPanel.add(messageInboxPanel, "MESSAGES");
        contentPanel.add(transactionHistoryPanel, "HISTORY");

        // Add components to main panel
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        mainFrame.add(mainPanel);

        mainFrame.setVisible(true);
    }

    private void createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(50, 50, 50));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        sidebarPanel.setPreferredSize(new Dimension(200, 0));

        // User info section at top
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBackground(new Color(70, 70, 70));
        userInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        userInfoPanel.setMaximumSize(new Dimension(200, 100));
        userInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Create user info labels
        usernameLabel = new JLabel("User: " + currentUser.getUserName());
        balanceLabel = new JLabel(String.format("Balance: $%.2f", currentUser.getBalance()));
        ratingLabel = new JLabel(String.format("Rating: %.1f (%d)", currentUser.getAverageSellerRating(), currentUser.getNumberOfRatings()));

        usernameLabel.setForeground(Color.WHITE);
        balanceLabel.setForeground(Color.WHITE);
        ratingLabel.setForeground(Color.WHITE);

        userInfoPanel.add(usernameLabel);
        userInfoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        userInfoPanel.add(balanceLabel);
        userInfoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        userInfoPanel.add(ratingLabel);

        // Add user info panel to sidebar
        sidebarPanel.add(userInfoPanel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Navigation buttons
        JButton profileButton = createMenuButton("Profile", "PROFILE");
        JButton buyButton = createMenuButton("Buy Items", "BUY");
        JButton sellButton = createMenuButton("Sell Items", "SELL");
        JButton historyButton = createMenuButton("Transaction History", "HISTORY");
        JButton messagesButton = createMenuButton("Messages", "MESSAGES");
        JButton logoutButton = createMenuButton("Logout", "LOGOUT");

        // Add navigation buttons to sidebar
        sidebarPanel.add(profileButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(buyButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(sellButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(historyButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(messagesButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Push logout to bottom
        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(logoutButton);
    }

    private JButton createMenuButton(String text, String action) {
        JButton button = new JButton(text);
        button.setActionCommand(action);
        button.setBackground(new Color(80, 80, 80));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 40));

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cmd = e.getActionCommand();

                if ("LOGOUT".equals(cmd)) {
                    currentUser = null;
                    mainFrame.dispose();
                    SwingUtilities.invokeLater(() -> createAuthGUI());
                } else if (contentPanel.getComponentCount() > 0) {
                    cardLayout.show(contentPanel, cmd);
                }
            }
        });

        return button;
    }

    private void createProfilePanel() {
        profilePanel = new JPanel();
        profilePanel.setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 240, 240));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("User Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        // Profile info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(6, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Profile info labels
        JLabel firstNameTitle = new JLabel("First Name:");
        firstNameLabel = new JLabel(currentUser.getFirstName());

        JLabel lastNameTitle = new JLabel("Last Name:");
        lastNameLabel = new JLabel(currentUser.getLastName());

        JLabel usernameTitle = new JLabel("Username:");
        profileUsernameLabel = new JLabel(currentUser.getUserName());

        JLabel balanceTitle = new JLabel("Current Balance:");
        profileBalanceLabel = new JLabel(String.format("$%.2f", currentUser.getBalance()));

        JLabel ratingTitle = new JLabel("Seller Rating:");
        sellerRatingLabel = new JLabel(String.format("%.1f", currentUser.getAverageSellerRating()));

        JLabel ratingsCountTitle = new JLabel("Number of Ratings:");
        numRatingsLabel = new JLabel(String.valueOf(currentUser.getNumberOfRatings()));

        infoPanel.add(firstNameTitle);
        infoPanel.add(firstNameLabel);
        infoPanel.add(lastNameTitle);
        infoPanel.add(lastNameLabel);
        infoPanel.add(usernameTitle);
        infoPanel.add(profileUsernameLabel);
        infoPanel.add(balanceTitle);
        infoPanel.add(profileBalanceLabel);
        infoPanel.add(ratingTitle);
        infoPanel.add(sellerRatingLabel);
        infoPanel.add(ratingsCountTitle);
        infoPanel.add(numRatingsLabel);

        // Create balance update panel
        JPanel updateBalancePanel = new JPanel();
        updateBalancePanel.setBorder(BorderFactory.createTitledBorder("Update Balance"));
        updateBalancePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel newBalanceLabel = new JLabel("New Balance: $");
        newBalanceField = new JTextField(10);
        updateBalanceButton = new JButton("Update");

        updateBalanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double newBalance = Double.parseDouble(newBalanceField.getText());
                    updateBalanceGUI(newBalance);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Please enter a valid number.",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        updateBalancePanel.add(newBalanceLabel);
        updateBalancePanel.add(newBalanceField);
        updateBalancePanel.add(updateBalanceButton);

        profilePanel.add(titlePanel, BorderLayout.NORTH);
        profilePanel.add(infoPanel, BorderLayout.CENTER);
        profilePanel.add(updateBalancePanel, BorderLayout.SOUTH);
    }

    private JPanel createBuyPanel() {
        JPanel buyPanel = new JPanel(new BorderLayout());

        // Create title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 240, 240));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Buy Items");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        // Search and filter panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        // Category filter dropdown
        String[] categories = {"All Categories", "Apparel", "Collectible", "Electronic", "Home", "Vehicle"};
        JComboBox<String> categoryFilter = new JComboBox<>(categories);

        // Search bar
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        searchPanel.add(new JLabel("Category:"));
        searchPanel.add(categoryFilter);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        buyPanel.add(searchPanel, BorderLayout.NORTH);

        // Items grid view
        itemsPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Display all items initially
        displayItems(marketplace.getAvailableItems(), itemsPanel);

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        buyPanel.add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().toLowerCase();
            String selectedCategory = (String) categoryFilter.getSelectedItem();

            List<Item> filteredItems = new ArrayList<>();
            List<Item> itemsToSearch = selectedCategory.equals("All Categories") ?
                    marketplace.getAvailableItems() : marketplace.searchByCategory(selectedCategory);

            for (Item item : itemsToSearch) {
                if (item.getName().toLowerCase().contains(searchTerm)) {
                    filteredItems.add(item);
                }
            }

            itemsPanel.removeAll();
            if (filteredItems.isEmpty()) {
                JLabel noItemsLabel = new JLabel("No items found in this category");
                noItemsLabel.setHorizontalAlignment(JLabel.CENTER);
                noItemsLabel.setFont(new Font("Arial", Font.BOLD, 16));
                itemsPanel.add(noItemsLabel);
            } else {
                displayItems(filteredItems, itemsPanel);
            }
            itemsPanel.revalidate();
            itemsPanel.repaint();
        });

        categoryFilter.addActionListener(e -> {
            String selectedCategory = (String) categoryFilter.getSelectedItem();
            itemsPanel.removeAll();
            List<Item> itemsToShow = selectedCategory.equals("All Categories") ?
                    marketplace.getAvailableItems() : marketplace.searchByCategory(selectedCategory);
            displayItems(itemsToShow, itemsPanel);
            itemsPanel.revalidate();
            itemsPanel.repaint();
        });

        return buyPanel;
    }

    private void displayItems(List<Item> items, JPanel itemDisplayPanel) {
        itemDisplayPanel.removeAll();

        if (items.isEmpty()) {
            JLabel noItemsLabel = new JLabel("No items found in this category");
            noItemsLabel.setHorizontalAlignment(JLabel.CENTER);
            noItemsLabel.setFont(new Font("Arial", Font.BOLD, 16));
            itemDisplayPanel.add(noItemsLabel);
            return;
        }

        for (Item item : items) {
            if (!item.isAvailable()) continue;

            JPanel itemCard = new JPanel(new BorderLayout());
            itemCard.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            itemCard.setPreferredSize(new Dimension(250, 350));

            // Item image
//            JLabel imageLabel = new JLabel(new ImageIcon(item.getImagePath()));
//            imageLabel.setPreferredSize(new Dimension(250, 150));
//            imageLabel.setHorizontalAlignment(JLabel.CENTER);

            // Item details
            JPanel detailsPanel = new JPanel();
            detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
            detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel nameLabel = new JLabel(item.getName());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

            JLabel priceLabel = new JLabel(String.format("$%.2f", item.getCost()));
            priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
            priceLabel.setForeground(Color.RED);

            JLabel sellerLabel = new JLabel("Seller: " + item.getSoldBy().getUserName());

            // Buttons panel
            JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 5, 0));

            JButton detailsButton = new JButton("Details");
            detailsButton.addActionListener(e -> showItemDetail(item));

            JButton buyButton = new JButton("Buy");
            buyButton.addActionListener(e -> {
                try {
                    attemptPurchase(item);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            buttonsPanel.add(detailsButton);
            buttonsPanel.add(buyButton);

            // Add components to details panel
            detailsPanel.add(nameLabel);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            detailsPanel.add(priceLabel);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            detailsPanel.add(sellerLabel);
            detailsPanel.add(Box.createVerticalGlue());
            detailsPanel.add(buttonsPanel);

            //itemCard.add(imageLabel, BorderLayout.NORTH);
            itemCard.add(detailsPanel, BorderLayout.CENTER);

            itemDisplayPanel.add(itemCard);
        }
    }

    private void showItemDetail(Item item) {
        JDialog detailDialog = new JDialog(mainFrame, "Item Details", true);
        detailDialog.setSize(600, 500);
        detailDialog.setLocationRelativeTo(mainFrame);

        JPanel detailPanel = new JPanel(new BorderLayout(10, 10));
        detailPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Large image
//        JLabel largeImage = new JLabel(new ImageIcon(item.getImagePath()));
//        largeImage.setHorizontalAlignment(JLabel.CENTER);
//        detailPanel.add(largeImage, BorderLayout.NORTH);

        // Details panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel priceLabel = new JLabel(String.format("Price: $%.2f", item.getCost()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel sellerLabel = new JLabel("Sold by: " + item.getSoldBy().getUserName());
        sellerLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JTextArea description = new JTextArea(item.toString());
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setEditable(false);
        description.setBackground(null);
        description.setFont(new Font("Arial", Font.PLAIN, 14));

        // Purchase button
        JButton purchaseButton = new JButton("Purchase");
        purchaseButton.addActionListener(e -> {
            detailDialog.dispose();
            try {
                attemptPurchase(item);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Add components
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(sellerLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        infoPanel.add(description);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        infoPanel.add(purchaseButton);

        detailPanel.add(infoPanel, BorderLayout.CENTER);
        detailDialog.add(detailPanel);
        detailDialog.setVisible(true);
    }

    private void attemptPurchase(Item item) throws IOException {
        if (currentUser.getBalance() < item.getCost()) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Insufficient balance for this purchase",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(mainFrame,
                String.format("Confirm purchase of %s for $%.2f?", item.getName(), item.getCost()),
                "Confirm Purchase",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = marketplace.purchaseItem(item, currentUser);

            if (success) {
                balanceLabel.setText(String.format("Current Balance: $%.2f", currentUser.getBalance()));
                profileBalanceLabel.setText(String.format("$%.2f", currentUser.getBalance()));

                JOptionPane.showMessageDialog(mainFrame,
                        "Purchase successful! Thank you for your order.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                // Prompt to rate seller
                promptForSellerRating(item.getSoldBy());

                // Update transaction history and refresh items
                updateTransactionHistory(item, currentUser);
                refreshItemsList();
            } else {
                JOptionPane.showMessageDialog(mainFrame,
                        "Purchase failed. Please try again later.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createSellPanel() {
        JPanel sellPanel = new JPanel(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 240, 240));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Sell Items");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        JPanel sellContentPanel = new JPanel();
        sellContentPanel.setLayout(new BoxLayout(sellContentPanel, BoxLayout.Y_AXIS));
        sellContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel instructionsLabel = new JLabel("List a new item for sale in the marketplace");
        instructionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        instructionsLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        // Create the sell item button
        JButton sellItemButton = new JButton("List New Item");
        sellItemButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        sellItemButton.setMaximumSize(new Dimension(200, 40));
        sellItemButton.setFont(new Font("Arial", Font.BOLD, 14));

        // Create refresh button
        JButton refreshButton = new JButton("Refresh List");
        refreshButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        refreshButton.setMaximumSize(new Dimension(200, 40));
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));


        // Button panel for List New Item and Refresh buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.add(sellItemButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(refreshButton);

        sellItemButton.addActionListener(e -> {
            try {
                sellItem();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(mainFrame,
                        "Error listing item: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshButton.addActionListener(e -> refreshItemsList());

        // Add your items list
        JLabel yourItemsLabel = new JLabel("Your Listed Items");
        yourItemsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        yourItemsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        yourItemsLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        // Create table for user's items
        String[] columns = {"Item", "Category", "Price", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        itemsTable = new JTable(tableModel);

        // Initial population of table with user's items
        List<Item> userItems = marketplace.getAvailableItems().stream()
                .filter(item -> item.getSoldBy().getUserName().equals(currentUser.getUserName()))
                .collect(Collectors.toList());

        for (Item item : userItems) {
            tableModel.addRow(new Object[]{
                    item.getName(),
                    item.getCategory(),
                    String.format("$%.2f", item.getCost()),
                    item.isAvailable() ? "Available" : "Sold"
            });
        }

        JScrollPane scrollPane = new JScrollPane(itemsTable);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setPreferredSize(new Dimension(400, 200));

        sellContentPanel.add(instructionsLabel);
        sellContentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        sellContentPanel.add(buttonPanel);
        sellContentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        sellContentPanel.add(yourItemsLabel);
        sellContentPanel.add(scrollPane);

        sellPanel.add(titlePanel, BorderLayout.NORTH);
        sellPanel.add(sellContentPanel, BorderLayout.CENTER);

        return sellPanel;
    }

    private void createTransactionHistoryPanel() {
        transactionHistoryPanel = new JPanel(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 240, 240));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Transaction History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        // Create tabbed pane for purchases and sales
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel purchasesPanel = new JPanel(new BorderLayout());
        purchasesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table models for dynamic updates
        String[] purchaseColumns = {"Date", "Item", "Category", "Seller", "Price"};
        purchasesTableModel = new DefaultTableModel(purchaseColumns, 0);
        purchasesTable = new JTable(purchasesTableModel);
        JScrollPane purchasesScrollPane = new JScrollPane(purchasesTable);
        purchasesPanel.add(purchasesScrollPane, BorderLayout.CENTER);

        JPanel salesPanel = new JPanel(new BorderLayout());
        salesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] salesColumns = {"Date", "Item", "Category", "Buyer", "Price"};
        salesTableModel = new DefaultTableModel(salesColumns, 0);
        salesTable = new JTable(salesTableModel);
        JScrollPane salesScrollPane = new JScrollPane(salesTable);
        salesPanel.add(salesScrollPane, BorderLayout.CENTER);

        // Add tabs to tabbed pane
        tabbedPane.addTab("Purchases", purchasesPanel);
        tabbedPane.addTab("Sales", salesPanel);

        transactionHistoryPanel.add(titlePanel, BorderLayout.NORTH);
        transactionHistoryPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    private void updateTransactionHistory(Item item, User buyer) {
        String date = java.time.LocalDate.now().toString();

        if (buyer.getUserName().equals(currentUser.getUserName())) {
            // Add to purchases table
            purchasesTableModel.addRow(new Object[]{
                    date,
                    item.getName(),
                    item.getCategory(),
                    item.getSoldBy().getUserName(),
                    String.format("$%.2f", item.getCost())
            });
        }

        if (item.getSoldBy().getUserName().equals(currentUser.getUserName())) {
            // Add to sales table
            salesTableModel.addRow(new Object[]{
                    date,
                    item.getName(),
                    item.getCategory(),
                    buyer.getUserName(),
                    String.format("$%.2f", item.getCost())
            });
        }
    }

    private void refreshItemsList() {
        // For sell panel
        DefaultTableModel tableModel = (DefaultTableModel) itemsTable.getModel();
        tableModel.setRowCount(0);
        List<Item> userItems = marketplace.getAvailableItems().stream()
                .filter(item -> item.getSoldBy().getUserName().equals(currentUser.getUserName()))
                .collect(Collectors.toList());

        for (Item item : userItems) {
            tableModel.addRow(new Object[]{
                    item.getName(),
                    item.getCategory(),
                    String.format("$%.2f", item.getCost()),
                    item.isAvailable() ? "Available" : "Sold"
            });
        }

        // For buy panel
        if (itemsPanel != null) {
            itemsPanel.removeAll();
            displayItems(marketplace.getAvailableItems(), itemsPanel);
            itemsPanel.revalidate();
            itemsPanel.repaint();
        }
    }

    private void updateBalanceGUI(double newBalance) {
        try {
            currentUser.setBalance(newBalance);

            // Update balance in file
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

            balanceLabel.setText(String.format("Balance: $%.2f", newBalance));
            profileBalanceLabel.setText(String.format("$%.2f", newBalance));

            // Clear input field
            newBalanceField.setText("");

            // Show success message
            JOptionPane.showMessageDialog(mainFrame,
                    "Balance updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Error updating balance: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
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

        // Common fields panel with improved styling
        JPanel commonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        commonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JComboBox<String> categoryCombo = new JComboBox<>(
                new String[]{"Apparel", "Collectible", "Electronic", "Home", "Vehicle"});

        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        priceField.setFont(new Font("Arial", Font.PLAIN, 14));
        categoryCombo.setFont(new Font("Arial", Font.PLAIN, 14));

        commonPanel.add(new JLabel("Item Name:"));
        commonPanel.add(nameField);
        commonPanel.add(new JLabel("Price:"));
        commonPanel.add(priceField);
        commonPanel.add(new JLabel("Category:"));
        commonPanel.add(categoryCombo);

        // Category panels
        JPanel cardPanel = new JPanel(new CardLayout());
        cardPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        // Apparel panel with dropdown for sizes
        JPanel apparelPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        String[] sizes = {"XS", "S", "M", "L", "XL"};
        JComboBox<String> sizeCombo = new JComboBox<>(sizes);
        JTextField colorField = new JTextField();
        JTextField brandField = new JTextField();

        sizeCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        colorField.setFont(new Font("Arial", Font.PLAIN, 14));
        brandField.setFont(new Font("Arial", Font.PLAIN, 14));

        apparelPanel.add(new JLabel("Size:"));
        apparelPanel.add(sizeCombo);
        apparelPanel.add(new JLabel("Color:"));
        apparelPanel.add(colorField);
        apparelPanel.add(new JLabel("Brand:"));
        apparelPanel.add(brandField);

        // Collectible panel
        JPanel collectiblePanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField cTypeField = new JTextField();
        JTextField conditionField = new JTextField();

        cTypeField.setFont(new Font("Arial", Font.PLAIN, 14));
        conditionField.setFont(new Font("Arial", Font.PLAIN, 14));

        collectiblePanel.add(new JLabel("Type:"));
        collectiblePanel.add(cTypeField);
        collectiblePanel.add(new JLabel("Condition:"));
        collectiblePanel.add(conditionField);

        // Electronic panel
        JPanel electronicPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField eTypeField = new JTextField();
        JSpinner yearSpinner = new JSpinner(
                new SpinnerNumberModel(2023, 1900, LocalDateTime.now().getYear(), 1));

        eTypeField.setFont(new Font("Arial", Font.PLAIN, 14));
        yearSpinner.setFont(new Font("Arial", Font.PLAIN, 14));

        electronicPanel.add(new JLabel("Type:"));
        electronicPanel.add(eTypeField);
        electronicPanel.add(new JLabel("Year:"));
        electronicPanel.add(yearSpinner);

        // Home panel
        JPanel homePanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JTextField hTypeField = new JTextField();
        hTypeField.setFont(new Font("Arial", Font.PLAIN, 14));

        homePanel.add(new JLabel("Type:"));
        homePanel.add(hTypeField);

        // Vehicle panel
        JPanel vehiclePanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JSpinner mileageSpinner = new JSpinner(
                new SpinnerNumberModel(0, 0, 1000000, 1000));
        JSpinner vYearSpinner = new JSpinner(
                new SpinnerNumberModel(2023, 1900, LocalDateTime.now().getYear(), 1));
        JTextField vBrandField = new JTextField();

        mileageSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        vYearSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        vBrandField.setFont(new Font("Arial", Font.PLAIN, 14));

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
            CardLayout cl = (CardLayout) (cardPanel.getLayout());
            cl.show(cardPanel, (String) categoryCombo.getSelectedItem());
        });

        // Submit button
        JButton submitButton = new JButton("List Item");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setBackground(new Color(70, 130, 180));
        submitButton.setForeground(Color.GREEN);
        submitButton.setFocusPainted(false);
        submitButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

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

                String category = (String) categoryCombo.getSelectedItem();
                String imagePath = ""; // Empty since we removed image functionality

                Item newItem;
                switch (category) {
                    case "Apparel":
                        newItem = new Apparel(name, price, currentUser, imagePath, category,
                                (String) sizeCombo.getSelectedItem(), colorField.getText(), brandField.getText());
                        break;
                    case "Collectible":
                        newItem = new Collectible(name, price, currentUser, imagePath, category,
                                cTypeField.getText(), conditionField.getText());
                        break;
                    case "Electronic":
                        newItem = new Electronic(name, price, currentUser, imagePath, category,
                                eTypeField.getText(), (int) yearSpinner.getValue());
                        break;
                    case "Home":
                        newItem = new Home(name, price, currentUser, imagePath, category,
                                hTypeField.getText());
                        break;
                    case "Vehicle":
                        newItem = new Vehicle(name, price, currentUser, imagePath, category,
                                (int) mileageSpinner.getValue(), (int) vYearSpinner.getValue(),
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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        buttonPanel.add(submitButton);

        frame.add(commonPanel, BorderLayout.NORTH);
        frame.add(cardPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * {@inheritDoc}
     * Handles the seller rating process after a successful purchase.
     */
    @Override
    public void promptForSellerRating(User seller) {
        SwingUtilities.invokeLater(() -> {
            JDialog ratingDialog = new JDialog(mainFrame, "Rate Seller", true);
            ratingDialog.setSize(350, 200);
            ratingDialog.setLayout(new BorderLayout());

            JPanel ratingPanel = new JPanel();
            ratingPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            ratingPanel.setLayout(new BoxLayout(ratingPanel, BoxLayout.Y_AXIS));

            // Display current rating info 
            JLabel currentRatingLabel = new JLabel(
                    String.format("Current rating: %.1f (%d ratings)",
                            seller.getAverageSellerRating(),
                            seller.getNumberOfRatings()));
            currentRatingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Rating selection
            JPanel starsPanel = new JPanel();
            starsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            starsPanel.add(new JLabel("Your rating: "));

            Integer[] ratings = {1, 2, 3, 4, 5};
            JComboBox<Integer> ratingCombo = new JComboBox<>(ratings);
            ratingCombo.setRenderer(new StarRatingRenderer());
            starsPanel.add(ratingCombo);

            // Submit button
            JButton submitButton = new JButton("Submit Rating");
            submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            submitButton.addActionListener(e -> {
                int newRating = (Integer) ratingCombo.getSelectedItem();

                // Add the new rating to the seller's ratings
                seller.addSellerRating(newRating, currentUser);

                // Update the seller's file
                try {
                    marketplace.updateUserData(seller);

                    // Update UI in real-time if the seller and buyer are the same user
                    if (seller.getUserName().equals(currentUser.getUserName())) {
                        ratingLabel.setText(String.format("Rating: %.1f (%d)",
                                currentUser.getAverageSellerRating(),
                                currentUser.getNumberOfRatings()));
                    }
                    
                    if (sellerToRate != null && sellerToRate.getUserName().equals(currentUser.getUserName())) {
                        sellerRatingLabel.setText(String.format("%.1f", seller.getAverageSellerRating()));
                        numRatingsLabel.setText(String.valueOf(seller.getNumberOfRatings()));
                    }

                    JOptionPane.showMessageDialog(ratingDialog,
                            "Thank you for your rating!\n" +
                                    String.format("Seller's new rating: %.1f (%d ratings)",
                                            seller.getAverageSellerRating(),
                                            seller.getNumberOfRatings()),
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                    ratingDialog.dispose();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(ratingDialog,
                            "Failed to save rating: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            ratingPanel.add(currentRatingLabel);
            ratingPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            ratingPanel.add(starsPanel);
            ratingPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            ratingPanel.add(submitButton);

            ratingDialog.add(ratingPanel, BorderLayout.CENTER);
            ratingDialog.setLocationRelativeTo(mainFrame);
            ratingDialog.setVisible(true);
        });
    }

    private void createMessageComposeWindow() {
        messageComposeFrame = new JFrame("Compose Message");
        messageComposeFrame.setSize(400, 300);
        messageComposeFrame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        recipientField = new JTextField();
        messageArea = new JTextArea();
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        inputPanel.add(new JLabel("To:"));
        inputPanel.add(recipientField);

        JScrollPane scrollPane = new JScrollPane(messageArea);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> {
            String recipient = recipientField.getText();
            String message = messageArea.getText();

            if (recipient.isEmpty() || message.isEmpty()) {
                JOptionPane.showMessageDialog(messageComposeFrame,
                        "Please fill in all fields",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!MarketplaceUser.userExists(recipient)) {
                JOptionPane.showMessageDialog(messageComposeFrame,
                        "User '" + recipient + "' does not exist",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            currentUser.sendMessageTo(recipient, message);
            JOptionPane.showMessageDialog(messageComposeFrame,
                    "Message sent successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            messageComposeFrame.dispose();
        });

        messageComposeFrame.add(inputPanel, BorderLayout.NORTH);
        messageComposeFrame.add(scrollPane, BorderLayout.CENTER);
        messageComposeFrame.add(sendButton, BorderLayout.SOUTH);

        messageComposeFrame.setLocationRelativeTo(null);
        messageComposeFrame.setVisible(true);
    }

    private void createMessageInboxPanel() {
        messageInboxPanel = new JPanel(new BorderLayout());
        messageInboxPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Message list on the left
        messageListModel = new DefaultListModel<>();
        messageList = new JList<>(messageListModel);
        messageList.setCellRenderer(new MessageCellRenderer());
        messageList.setPreferredSize(new Dimension(200, 0));

        // Message view on the right
        messageViewArea = new JTextArea();
        messageViewArea.setEditable(false);
        messageViewArea.setLineWrap(true);
        messageViewArea.setWrapStyleWord(true);

        // Split pane to divide list and view
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(messageList),
                new JScrollPane(messageViewArea)
        );
        splitPane.setDividerLocation(200);

        // Compose button at the top
        JButton composeButton = new JButton("Compose New Message");
        composeButton.addActionListener(e -> createMessageComposeWindow());

        // Add components to panel
        messageInboxPanel.add(composeButton, BorderLayout.NORTH);
        messageInboxPanel.add(splitPane, BorderLayout.CENTER);

        // Load messages
        refreshMessages();

        // Add selection listener
        messageList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = messageList.getSelectedIndex();
                if (index != -1) {
                    String message = messageList.getModel().getElementAt(index);
                    messageViewArea.setText(message);
                }
            }
        });

        // Add to content panel
        contentPanel.add(messageInboxPanel, "MESSAGES");
    }

    private void refreshMessages() {
        messageListModel.clear();
        ArrayList<String> messages = currentUser.viewMessages();

        if (messages.isEmpty()) {
            messageListModel.addElement("No messages");
            messageViewArea.setText("");
        } else {
            for (String message : messages) {
                messageListModel.addElement(message);
            }
        }
    }

    // Custom renderer for messages in the list
    private class MessageCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            String message = value.toString();
            if (!message.equals("No messages")) {
                String[] lines = message.split("\n");
                if (lines.length >= 2) {
                    String sender = lines[0].replace("FROM: ", "");
                    String date = lines[1].replace("DATE: ", "");
                    String preview = lines.length > 2 ? lines[2] : "";
                    if (preview.length() > 30) {
                        preview = preview.substring(0, 27) + "...";
                    }
                    label.setText(String.format("<html><b>%s</b><br/>%s<br/>%s</html>",
                            sender, date, preview));
                }
            }

            return label;
        }
    }

    // Custom renderer for star ratings
    private class StarRatingRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            int rating = (Integer) value;
            StringBuilder stars = new StringBuilder();
            for (int i = 0; i < rating; i++) {
                stars.append("★");
            }
            for (int i = rating; i < 5; i++) {
                stars.append("☆");
            }

            label.setText(stars.toString());
            return label;
        }
    }
}

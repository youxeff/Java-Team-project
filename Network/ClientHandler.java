package Network;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import javax.swing.*;
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
    private DefaultListModel<String> purchasesListModel;
    private DefaultListModel<String> salesListModel;

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
    private JDialog ratingDialog;
    private JComboBox<Integer> ratingComboBox;
    private User sellerToRate;

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
        JPanel welcomePanel = new JPanel(new BorderLayout());

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register New User");
        JButton exitButton = new JButton("Exit");

        loginButton.addActionListener(e -> authCardLayout.show(cards, "Login"));
        registerButton.addActionListener(e -> authCardLayout.show(cards, "Register"));
        exitButton.addActionListener(e -> authFrame.dispose());

        JPanel centerButtons = new JPanel(new GridLayout(3, 1, 10, 10));
        centerButtons.add(loginButton);
        centerButtons.add(registerButton);
        centerButtons.add(exitButton);

        welcomePanel.add(centerButtons, BorderLayout.CENTER);
        return welcomePanel;
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
                SwingUtilities.invokeLater(() -> createAndShowGUI());
            } else {
                JOptionPane.showMessageDialog(authFrame, "Invalid username or password.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(authFrame, "Error during login: " + e.getMessage());
        }
    }

    private void createAndShowGUI() {
        mainFrame = new JFrame("Marketplace System");
        mainFrame.setSize(900, 600);
        mainFrame.setMinimumSize(new Dimension(800, 500));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        
        // Create content panel with category selection
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        JLabel instructionsLabel = new JLabel("Browse items by category");
        instructionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        instructionsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        // Create category buttons
        String[] categories = {"Apparel", "Collectible", "Electronic", "Home", "Vehicle"};
        JPanel categoryButtonsPanel = new JPanel(new GridLayout(5, 1, 0, 10));
        categoryButtonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        categoryButtonsPanel.setMaximumSize(new Dimension(300, 250));
        
        for (String category : categories) {
            JButton categoryButton = new JButton(category);
            categoryButton.setFont(new Font("Arial", Font.BOLD, 14));
            
            categoryButton.addActionListener(e -> {
                try {
                    buyItem();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(mainFrame, 
                        "Error browsing items: " + ex.getMessage(),
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            categoryButtonsPanel.add(categoryButton);
        }
        
        // Add components to content panel
        contentPanel.add(instructionsLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(categoryButtonsPanel);
        
        // Add panels to main buy panel
        buyPanel.add(titlePanel, BorderLayout.NORTH);
        buyPanel.add(contentPanel, BorderLayout.CENTER);
        
        return buyPanel;
    }

    private JPanel createSellPanel() {
        JPanel sellPanel = new JPanel(new BorderLayout());
        
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 240, 240));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Sell Items");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        JLabel instructionsLabel = new JLabel("List a new item for sale in the marketplace");
        instructionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        instructionsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        // Create the sell item button
        JButton sellItemButton = new JButton("List New Item");
        sellItemButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        sellItemButton.setMaximumSize(new Dimension(200, 40));
        sellItemButton.setFont(new Font("Arial", Font.BOLD, 14));
        
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
        
        // Add your items list
        JLabel yourItemsLabel = new JLabel("Your Listed Items");
        yourItemsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        yourItemsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        yourItemsLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        // Temp listed items
        String[] columns = {"Item", "Category", "Price", "Status"};
        Object[][] data = {
            {"Sample Item 1", "Electronics", "$1500", "Available"},
            {"Sample Item 2", "Apparel", "$45", "Available"},
            {"Sample Item 3", "Home", "$1209999", "Sold"}
        };
        
        JTable itemsTable = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        
        contentPanel.add(instructionsLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(sellItemButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(yourItemsLabel);
        contentPanel.add(scrollPane);
        
        sellPanel.add(titlePanel, BorderLayout.NORTH);
        sellPanel.add(contentPanel, BorderLayout.CENTER);
        
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
        
        // Temporary purchases table
        String[] purchaseColumns = {"Date", "Item", "Category", "Seller", "Price"};
        Object[][] purchaseData = {
            {"01/05/2025", "mango", "Electronics", "user123", "$99.99"},
            {"01/01/1776", "arizona green tea honey ginseng flavor", "Apparel", "e", "$45.00"}
        };
        purchasesTable = new JTable(purchaseData, purchaseColumns);
        JScrollPane purchasesScrollPane = new JScrollPane(purchasesTable);
        purchasesPanel.add(purchasesScrollPane, BorderLayout.CENTER);
        
        JPanel salesPanel = new JPanel(new BorderLayout());
        salesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Temporary sales table
        String[] salesColumns = {"Date", "Item", "Category", "Buyer", "Price"};
        Object[][] salesData = {
            {"05/02/2025", "Sample Item 3", "Home", "joebob", "$120.50"},
            {"100/100/2025", "Sample Item 4", "Collectible", "waaaaaa", "$350.00"}
        };
        salesTable = new JTable(salesData, salesColumns);
        JScrollPane salesScrollPane = new JScrollPane(salesTable);
        salesPanel.add(salesScrollPane, BorderLayout.CENTER);
        
        // Add tabs to tabbed pane
        tabbedPane.addTab("Purchases", purchasesPanel);
        tabbedPane.addTab("Sales", salesPanel);
        
        transactionHistoryPanel.add(titlePanel, BorderLayout.NORTH);
        transactionHistoryPanel.add(tabbedPane, BorderLayout.CENTER);
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
        SwingUtilities.invokeLater(() -> showRatingDialog(seller));
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
    
    private void showRatingDialog(User seller) {
        sellerToRate = seller;
        ratingDialog = new JDialog(mainFrame, "Rate Seller", true);
        ratingDialog.setSize(300, 150);
        ratingDialog.setLayout(new BorderLayout());
        
        JPanel ratingPanel = new JPanel(new FlowLayout());
        
        // Rating combo box with stars
        Integer[] ratings = {1, 2, 3, 4, 5};
        ratingComboBox = new JComboBox<>(ratings);
        ratingComboBox.setRenderer(new StarRatingRenderer());
        
        JButton submitButton = new JButton("Submit Rating");
        submitButton.addActionListener(e -> submitRating());
        
        ratingPanel.add(new JLabel("Rating: "));
        ratingPanel.add(ratingComboBox);
        
        ratingDialog.add(ratingPanel, BorderLayout.CENTER);
        ratingDialog.add(submitButton, BorderLayout.SOUTH);
        
        ratingDialog.setLocationRelativeTo(mainFrame);
        ratingDialog.setVisible(true);
    }
    
    private void submitRating() {
        int rating = (Integer) ratingComboBox.getSelectedItem();
        if (sellerToRate.addSellerRating(rating, currentUser)) {
            JOptionPane.showMessageDialog(ratingDialog,
                "Rating submitted successfully!\n" +
                String.format("Seller's current rating: %.1f (%d ratings)",
                    sellerToRate.getAverageSellerRating(),
                    sellerToRate.getNumberOfRatings()),
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            ratingDialog.dispose();
        } else {
            JOptionPane.showMessageDialog(ratingDialog,
                "Failed to submit rating",
                "Error",
                JOptionPane.ERROR_MESSAGE);
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

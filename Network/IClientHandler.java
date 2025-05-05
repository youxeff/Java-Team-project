package Network;

import java.io.IOException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import model.users.User;
import model.items.Item;

/**
 * Interface defining the client handler functionality for the marketplace system.
 * Handles individual client connections and their interactions with the marketplace.
 * 
 * @author Youssef Abdelkader
 * @author Anthony Kim
 * @author Isaac Yoon
 * @author Caroline Murphy
 * @author Eric Yen
 * @version May 4, 2025
 */
public interface IClientHandler {
    // Core functionality methods
    void run();
    void sellItem() throws IOException;
    void promptForSellerRating(User seller) throws IOException;

    // GUI methods
    void createAuthGUI();
    void createAndShowGUI();
    void createMessageComposeWindow();
    void createMessageInboxPanel();
    void createProfilePanel();
    void createTransactionHistoryPanel();

    // Helper methods
    void displayItems(List<Item> items, JPanel targetItemsPanel);
    void updateTransactionHistory(Item item, User buyer);
    void refreshItemsList();
    void updateBalanceGUI(double newBalance);
    void refreshMessages();
    void register();
    void login();
    void loadUserTransactions();
    void createSidebar();

    // GUI component creation methods
    JButton createStyledButton(String text, Color backgroundColor);
    JPanel createBuyPanel();
    JPanel createSellPanel();
    JPanel welcomePanel();
    JPanel registerPanel();
    JPanel loginPanel();

    // Action methods
    void showItemDetail(Item item);
    void attemptPurchase(Item item) throws IOException;
}

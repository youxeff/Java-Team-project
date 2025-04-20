package Main;

/**
 * Interface defining the core user interaction methods for the Marketplace system.
 * Implemented by the Main class to handle user operations.
 */
public interface IMain {

    /**
     * Handles new user registration process.
     */
    void registerUser();

    /**
     * Manages user authentication and login.
     */
    void loginUser();

    /**
     * Displays and manages the logged-in user's menu options.
     */
    void showUserMenu();

    /**
     * Shows the current user's profile information.
     */
    void displayUserProfile();

    /**
     * Updates the user's account balance.
     */
    void updateBalance();

    /**
     * Provides options for buying or selling items.
     */
    void buyOrSell();

    /**
     * Handles the item selling process.
     */
    void sellItem();

    /**
     * Handles the item purchasing process.
     */
    void buyItem();
}

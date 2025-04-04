import java.util.Scanner;

public class Testing {
    private static Scanner scanner = new Scanner(System.in);
    private static MarketplaceUser currentUser = null;

    public static void main(String[] args) {
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

    private static void registerUser() {
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

            currentUser = new MarketplaceUser(firstName, lastName, username, password);
            if (currentUser != null) {
                System.out.println("Registration successful!");
                showUserMenu();
            }
        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
        }
    }

    private static void loginUser() {
        try {
            System.out.println("\n=== User Login ===");
            System.out.print("Enter Username: ");
            String username = scanner.nextLine();
            
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            if (MarketplaceUser.verifyCredentials(username, password)) {
                currentUser = new MarketplaceUser("temp", "temp", username, password);
                System.out.println("Login successful!");
                showUserMenu();
            } else {
                System.out.println("Login failed. Invalid credentials.");
            }
        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
        }
    }

    private static void showUserMenu() {
        boolean userMenuRunning = true;
        while (userMenuRunning && currentUser != null) {
            System.out.println("\n=== User Menu ===");
            System.out.println("1. View Profile");
            System.out.println("2. Update Balance");
            System.out.println("3. Logout");
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

    private static void displayUserProfile() {
        System.out.println("\n=== User Profile ===");
        System.out.println("First Name: " + currentUser.getFirstName());
        System.out.println("Last Name: " + currentUser.getLastName());
        System.out.println("Username: " + currentUser.getUserName());
        System.out.println("Balance: $" + String.format("%.2f", currentUser.getBalance()));
    }

    private static void updateBalance() {
        try {
            System.out.print("Enter new balance amount: $");
            double newBalance = Double.parseDouble(scanner.nextLine());
            currentUser.setBalance(newBalance);
            System.out.println("Balance updated successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }
}

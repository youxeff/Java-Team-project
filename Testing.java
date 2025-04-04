import java.util.Scanner;

public class Testing {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MarketplaceUser user = null;

        System.out.println("Would you like to register as:" +
                "\n1. Seller" +
                "\n2. Buyer" +
                "\n3. Both");
        int userType = scanner.nextInt();
        scanner.nextLine();

        Role initialRole;
        switch (userType) {
            case 1:
                initialRole = Role.SELLER;
                break;
            case 2:
                initialRole = Role.BUYER;
                break;
            case 3:
                initialRole = Role.BOTH;
                break;
            default:
                System.out.println("Invalid choice. Defaulting to BUYER.");
                initialRole = Role.BUYER;
                break;
        }

        try {
            System.out.println("\nEnter First Name:");
            String firstName = scanner.nextLine();

            System.out.println("Enter Last Name:");
            String lastName = scanner.nextLine();

            System.out.println("Enter Username:");
            String username = scanner.nextLine();

            System.out.println("Enter Password:");
            String password = scanner.nextLine();

            user = MarketplaceUser.registerNewUser(firstName, lastName, username, password, initialRole);

            System.out.println("\nUser Details:");
            System.out.println("First Name: " + user.getFirstName());
            System.out.println("Last Name: " + user.getLastName());
            System.out.println("Username: " + user.getUserName());
            System.out.println("Password: " + user.getPassword());
            System.out.println("Balance: " + user.getBalance());
            System.out.println("Role: " + user.getRole());

            System.out.println("Enter amount to update balance:");
            double newBalance = Double.parseDouble(scanner.nextLine());
            user.setBalance(newBalance);
            System.out.println("Updated Balance: " + user.getBalance());

        } catch (Exception e) {
            System.out.println("An error occurred during input: " + e.getMessage());
        }

        // User login
        if (user != null) {
            while (true) {
                try {
                    System.out.println("\nLogin:");
                    System.out.println("Enter Username:");
                    String loginUsername = scanner.nextLine();
                    System.out.println("Enter Password:");
                    String loginPassword = scanner.nextLine();

                    boolean loginSuccess = user.login(loginUsername, loginPassword);
                    if (loginSuccess) {
                        System.out.println("Login successful!");
                        break;
                    } else {
                        System.out.println("Login failed. Try again.");
                    }
                } catch (Exception e) {
                    System.out.println("An error occurred during login: " + e.getMessage());
                }
            }
        } else {
            System.out.println("Registration failed. Cannot attempt login.");
        }

        scanner.close();
    }
}

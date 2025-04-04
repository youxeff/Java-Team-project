import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Seller seller = null;
        Buyer buyer = null;

        System.out.println("Would you like to register as a Seller or Buyer? (Enter 'seller' or 'buyer'):");
        String userType = scanner.nextLine().trim().toLowerCase();

        if (userType.equals("seller")) {
            try {
                // Seller input
                System.out.println("Enter Seller First Name:");
                String sellerFirstName = scanner.nextLine();

                System.out.println("Enter Seller Last Name:");
                String sellerLastName = scanner.nextLine();

                System.out.println("Enter Seller Username:");
                String sellerUsername = scanner.nextLine();

                System.out.println("Enter Seller Password:");
                String sellerPassword = scanner.nextLine();

                seller = Seller.registerNewSeller(sellerFirstName, sellerLastName, sellerUsername, sellerPassword);

                System.out.println("\nSeller Details:");
                System.out.println("First Name: " + seller.getFirstName());
                System.out.println("Last Name: " + seller.getLastName());
                System.out.println("Username: " + seller.getUserName());
                System.out.println("Password: " + seller.getPassword());
                System.out.println("Balance: " + seller.getBalance());

                System.out.println("Enter amount to update Seller balance:");
                double newSellerBalance = Double.parseDouble(scanner.nextLine());
                seller.setBalance(newSellerBalance);
                System.out.println("Updated Balance: " + seller.getBalance());
            } catch (Exception e) {
                System.out.println("An error occurred during input: " + e.getMessage());
            }

            // Seller login
            if (seller != null) {
                while (true) {
                    try {
                        System.out.println("\nLogin as Seller:");
                        System.out.println("Enter Username:");
                        String loginSellerUsername = scanner.nextLine();
                        System.out.println("Enter Password:");
                        String loginSellerPassword = scanner.nextLine();
                        boolean sellerLoginSuccess = seller.login(loginSellerUsername, loginSellerPassword);
                        if (sellerLoginSuccess) {
                            System.out.println("Seller login successful: ");
                            break;
                        } else {
                            System.out.println("Seller login failed: ");
                        }
                    } catch (Exception e) {
                        System.out.println("An error occurred during input: " + e.getMessage());
                    }
                }
            } else {
                System.out.println("Seller registration failed. Cannot attempt login.");
            }
        } else if (userType.equals("buyer")) {
            try {
                // Buyer input
                System.out.println("\nRegister as Buyer:");

                System.out.println("\nEnter Buyer First Name:");
                String buyerFirstName = scanner.nextLine();

                System.out.println("Enter Buyer Last Name:");
                String buyerLastName = scanner.nextLine();

                System.out.println("Enter Buyer Username:");
                String buyerUsername = scanner.nextLine();

                System.out.println("Enter Buyer Password:");
                String buyerPassword = scanner.nextLine();

                buyer = Buyer.registerNewSeller(buyerFirstName, buyerLastName, buyerUsername, buyerPassword);

                System.out.println("\nBuyer Details:");
                System.out.println("First Name: " + buyer.getFirstName());
                System.out.println("Last Name: " + buyer.getLastName());
                System.out.println("Username: " + buyer.getUserName());
                System.out.println("Password: " + buyer.getPassword());
                System.out.println("Balance: " + buyer.getBalance());

                System.out.println("Enter amount to update Buyer balance:");
                double newBuyerBalance = Double.parseDouble(scanner.nextLine());
                buyer.setBalance(newBuyerBalance);
                System.out.println("Updated Balance: " + buyer.getBalance());
            } catch (Exception e) {
                System.out.println("An error occurred during input: " + e.getMessage());
            }

            // Buyer login
            if (buyer != null) {
                while (true) {
                    try {
                        System.out.println("\nLogin as Buyer:");
                        System.out.println("Enter Username:");
                        String loginBuyerUsername = scanner.nextLine();
                        System.out.println("Enter Password:");
                        String loginBuyerPassword = scanner.nextLine();

                        boolean buyerLoginSuccess = buyer.login(loginBuyerUsername, loginBuyerPassword);
                        if (buyerLoginSuccess) {
                            System.out.println("Buyer login successful: ");
                            break;
                        } else {
                            System.out.println("Buyer login failed: ");
                        }
                    } catch (Exception e) {
                        System.out.println("An error occurred during input: " + e.getMessage());
                    }
                }
            } else {
                System.out.println("Buyer registration failed. Cannot attempt login.");
            }
        } else {
            System.out.println("Invalid input. Please restart and enter either 'seller' or 'buyer'.");
        }

        scanner.close();
    }
}

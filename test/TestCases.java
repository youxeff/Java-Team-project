package test;

import Network.ClientHandler;
import Service.Marketplace;
import model.items.*;
import model.users.MarketplaceUser;
import model.users.User;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the Marketplace system.
 * Contains nested test classes for each major component.
 *
 * @author Youssef Abdelkader
 * @author Anthony Kim
 * @author Caroline Murphy
 * @author Eric Yen
 * @author Isaac Yoon
 * @version April 20 2025
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestCases {
    /** File paths used for test data */
    private static final String USERS_FILE = "users.txt";
    private static final String MESSAGES_DIR = "messages";
    private static final String ITEMS_FILE = "items.txt";

    /**
     * Tests for MarketplaceUser functionality including:
     * user creation, authentication, messaging, and data persistence.
     */
    @Nested
    class MarketplaceUserTest {

        /**
         * Sets up the test environment by deleting test files before each test.
         * @throws IOException if an I/O error occurs while deleting files.
         */
        @BeforeEach
        void setUp() throws IOException {
            File usersFile = new File(USERS_FILE);
            if (usersFile.exists()) {
                usersFile.delete();
            }
            File messagesDir = new File(MESSAGES_DIR);
            if (!messagesDir.exists()) {
                messagesDir.delete();
            }
        }

        /**
         * Cleans up the test environment by deleting test files after each test.
         */
        @AfterEach
        void tearDown() {
            File usersFile = new File(USERS_FILE);
            if (usersFile.exists()) {
                usersFile.delete();
            }
            File messagesDir = new File(MESSAGES_DIR);
            if (messagesDir.exists()) {
                File[] files = messagesDir.listFiles();
                if (files != null) { // Add null check
                    for (File file : files) {
                        file.delete();
                    }
                }
                messagesDir.delete();
            }
        }

        /**
         * Tests the creation of a new user.
         * Ensures that a user cannot create a duplicate account.
         */
        @Test
        void testUserCreation() {
            MarketplaceUser user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            assertFalse(user.createNewUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword"));
        }

        /**
         * Tests the creation of a user with empty fields.
         * Ensures that user creation fails when required fields are empty.
         */
        @Test
        void testUserCreationWithEmptyFields() {
            MarketplaceUser user = new MarketplaceUser("", "Peter", "peterEmpty", "notAStrongPassword");
            assertFalse(user.createNewUser("", "Peter", "peterEmpty", "notAStrongPassword"));
        }

        /**
         * Tests the creation of a duplicate user.
         * Ensures that creating a user with an existing username fails.
         */
        @Test
        void testCreateDuplicateUser() {
            MarketplaceUser user1 = new MarketplaceUser("Youssef", "Abdelkader", "youxeff", "pASsWord", 0, true);
            boolean user1Exists = user1.createNewUser("Youssef", "Abdelkader", "youxeff", "pASsWord");
            assertTrue(user1Exists);

            MarketplaceUser user2 = new MarketplaceUser("Youssef", "Abdelkader", "youxeff", "pASsWord", 0, true);
            boolean user2Exists = user2.createNewUser("Youssef", "Abdelkader", "youxeff", "pASsWord");
            assertFalse(user2Exists);

            MarketplaceUser loadedUser = MarketplaceUser.loadUser("youxeff");
            assertNotNull(loadedUser);
            assertEquals("youxeff", loadedUser.getUserName());
        }

        /**
         * Tests successful login for a user.
         * Ensures that a user can log in with correct credentials.
         */
        @Test
        void testLoginSuccess() {
            MarketplaceUser user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            assertTrue(user.login("iyoon", "thisIsAStrongPassword"));
        }


        /**
         * Tests login failure with incorrect credentials.
         * Ensures that login fails when the password is incorrect.
         */
        @Test
        void testLoginFails() {
            MarketplaceUser user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            assertFalse(user.login("iyoon", "wrongPassword"));
        }

        /**
         * Tests sending a message to another user.
         * Ensures that the message file is created successfully.
         */
        @Test
        void testSendMessageToUser() {
            MarketplaceUser user1 = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            MarketplaceUser user2 = new MarketplaceUser("Arjun", "Anilkumar", "aanil", "anotherStrongPassword");

            user1.sendMessageTo("aanil", "Hello, Isaac!");
            File messageFile = new File("messages/aanil.txt");
            assertTrue(messageFile.exists());
        }

        /**
         * Tests viewing messages sent to a user.
         * Ensures that the message content is correct.
         * @throws IOException if an I/O error occurs while reading the message file.
         */
        @Test
        void testViewMessages() throws IOException {

            MarketplaceUser user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            MarketplaceUser user2 = new MarketplaceUser("Arjun", "Anilkumar", "aanil", "anotherStrongPassword");

            user.sendMessageTo("aanil", "Hello, Isaac!");

            File messageFile = new File("messages/aanil.txt");
            assertTrue(messageFile.exists());

            BufferedReader br = new BufferedReader(new FileReader("messages/aanil.txt"));
            String content = br.readLine();
            assertEquals(content, "FROM: iyoon");

            content = br.readLine(); // Ignore date and time check
            content = br.readLine();

            assertEquals(content, "Hello, Isaac!");
        }

        /**
         * Tests whether a user exists in the system.
         * Ensures that the userExists method works correctly.
         */
        @Test
        void testUserExists() {
            MarketplaceUser user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword", 0, true);
            boolean userCreated = user.createNewUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            assertTrue(userCreated);

            MarketplaceUser loadedUser = MarketplaceUser.loadUser("iyoon");
            assertFalse(user.userExists("no user"));

            assertTrue(loadedUser.userExists("iyoon"));

            assertFalse(loadedUser.userExists(""));

            assertFalse(loadedUser.userExists(null));
        }

        /**
         * Tests loading a user from the system.
         * Ensures that the user is loaded correctly with all attributes.
         */
        @Test
        void testLoadUser() {
            MarketplaceUser user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            user.createNewUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");

            MarketplaceUser loadedUser = MarketplaceUser.loadUser("iyoon");
            assertNotNull(loadedUser);
            assertEquals("Isaac", loadedUser.getFirstName());
            assertEquals("Yoon", loadedUser.getLastName());
            assertEquals("iyoon", loadedUser.getUserName());
        }
    }

    /**
     * Tests for Marketplace service functionality including:
     * user management, item operations, and search features.
     */
    @Nested
    class MarketplaceTest {
        private static final String USERS_FILE = "users.txt";
        private static final String ITEMS_FILE = "items.txt";
        private Marketplace marketplace;
        private MarketplaceUser testUser;

        /**
         * Sets up the test environment by creating necessary files and initializing the marketplace.
         * @throws IOException if an I/O error occurs while creating files.
         */
        @BeforeEach
        void setUp() throws IOException {
            new File(USERS_FILE).createNewFile();
            new File(ITEMS_FILE).createNewFile();

            marketplace = new Marketplace();
            testUser = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAPassword");
            marketplace.updateUserData(testUser);
        }

        /**
         * Cleans up the test environment by deleting test files after each test.
         */
        @AfterEach
        void tearDown() {
            new File(USERS_FILE).delete();
            new File(ITEMS_FILE).delete();
        }

        /**
         * Tests updating user data in the marketplace.
         * Ensures that the user data is saved and loaded correctly.
         * @throws IOException if an I/O error occurs while loading user data.
         */
        @Test
        void testUpdateUserData() throws IOException {
            ArrayList<User> users = marketplace.loadAllUsers();
            assertEquals(1, users.size());
            assertEquals("Isaac", users.get(0).getFirstName());
        }

        /**
         * Tests searching for a seller by name.
         * Ensures that the correct seller is returned.
         */
        @Test
        void testSearchSeller() {
            ArrayList<User> searchResults = marketplace.searchSeller("Isaac");
            assertEquals(1, searchResults.size());
            assertEquals("iyoon", searchResults.get(0).getUserName());
        }

        /**
         * Tests authenticating a user in the marketplace.
         * Ensures that the user is authenticated with correct credentials.
         */
        @Test
        void testAuthenticateUser() {
            User authenticatedUser = marketplace.authenticateUser("iyoon", "thisIsAPassword");
            assertNotNull(authenticatedUser);
            assertEquals("Isaac", authenticatedUser.getFirstName());
        }

        /**
         * Tests adding an item to the marketplace.
         * Ensures that the item is added and can be searched by name.
         */
        @Test
        void testAddItem() {
            AbstractItem item = new Electronic("Laptop", 1000.0, testUser, "laptop.png", "Electronics", "Gaming", 2023);
            marketplace.addItem(item);

            ArrayList<Item> resultsByName = marketplace.searchByName("Laptop");
            assertEquals(1, resultsByName.size());
            assertEquals("Laptop", resultsByName.get(0).getName());
        }

        /**
         * Tests searching for items by name.
         * Ensures that the correct items are returned.
         */
        @Test
        void testSearchByName() {
            AbstractItem item = new Electronic("Laptop", 1000.0, testUser, "laptop.png", "Electronics", "Gaming", 2023);
            marketplace.addItem(item);

            ArrayList<Item> results = marketplace.searchByName("Laptop");
            assertEquals(1, results.size());
            assertEquals("Laptop", results.get(0).getName());
        }

        /**
         * Tests searching for items by category.
         * Ensures that the correct items are returned.
         */
        @Test
        void testSearchByCategory() {
            AbstractItem item = new Electronic("Laptop", 1000.0, testUser, "laptop.png", "Electronics", "Gaming", 2023);
            marketplace.addItem(item);

            ArrayList<Item> results = marketplace.searchByCategory("Electronics");
            assertEquals(1, results.size());
            assertEquals("Electronics", results.get(0).getCategory());
        }

        /**
         * Tests retrieving all available items in the marketplace.
         * Ensures that only available items are returned.
         */
        @Test
        void testGetAvailableItems() {
            AbstractItem item = new Electronic("Laptop", 1000.0, testUser, "laptop.png", "Electronics", "Gaming", 2023);
            marketplace.addItem(item);

            ArrayList<Item> availableItems = marketplace.getAvailableItems();
            assertEquals(1, availableItems.size());
        }

        /**
         * Tests purchasing an item from the marketplace.
         * Ensures that the item is marked as sold and removed from available items.
         * @throws IOException if an I/O error occurs while updating user data.
         */
        @Test
        void testPurchaseItem() throws IOException {
            AbstractItem item = new Electronic("Laptop", 1000.0, testUser, "laptop.png", "Electronics", "Gaming", 2023);
            marketplace.addItem(item);

            MarketplaceUser buyer = new MarketplaceUser("John", "Doe", "jdoe", "password456", 4000.0, true);
            marketplace.updateUserData(buyer);

            boolean purchaseResult = marketplace.purchaseItem(item, buyer);
            assertTrue(purchaseResult);

            ArrayList<Item> availableItems = marketplace.getAvailableItems();
            assertEquals(0, availableItems.size());
        }
    }

    /**
     * Tests for AbstractItem base class functionality including:
     * basic item operations and property management.
     */
    @Nested
    class AbstractItemTest {

        /**
         * Test setup for AbstractItem.
         * Initializes a seller, a buyer, and a test item.
         */
        private AbstractItem testItem;
        private MarketplaceUser seller;
        private MarketplaceUser buyer;

        /**
         * Sets up the test environment by initializing a seller, a buyer, and a test item.
         */
        @BeforeEach
        void setUp() {
            seller = new MarketplaceUser("Seller", "Test", "seller123", "thisIsAPassword", 100.0, true);
            buyer = new MarketplaceUser("Buyer", "Test", "buyer123", "password", 50.0, true);

            testItem = new AbstractItem("Test Item", 30.0, seller, "testImage.png", "Test Category") {
            };
        }

        /**
         * Tests the successful sale of an item.
         * Ensures that the buyer's balance is reduced, the seller's balance is increased,
         * and the item is marked as sold.
         */
        @Test
        void testSellItemSuccess() {
            boolean result = testItem.sellItem(buyer);
            assertTrue(result);
            assertEquals(20.0, buyer.getBalance());
            assertEquals(130.0, seller.getBalance());
            assertFalse(testItem.isAvailable());
        }

        /**
         * Tests the sale of an item when the buyer has insufficient balance.
         * Ensures that the sale does not proceed and the item remains available.
         */
        @Test
        void testSellItemInsufficientBalance() {
            buyer.setBalance(10.0);
            boolean result = testItem.sellItem(buyer);
            assertFalse(result);
            assertEquals(10.0, buyer.getBalance());
            assertEquals(100.0, seller.getBalance());
            assertTrue(testItem.isAvailable());
        }

        /**
         * Tests the sale of an item that has already been sold.
         * Ensures that the sale does not proceed and the item remains unavailable.
         */
        @Test
        void testSellItemAlreadySold() {
            testItem.sellItem(buyer);
            boolean result = testItem.sellItem(buyer);
            assertFalse(result);
        }

        /**
         * Tests deleting an item.
         * Ensures that the item is marked as unavailable after deletion.
         */
        @Test
        void testDeleteItem() {
            boolean result = testItem.deleteItem();
            assertTrue(result);
            assertFalse(testItem.isAvailable());
        }

        /**
         * Tests marking an item as sold.
         * Ensures that the item is marked as unavailable.
         */
        @Test
        void testMarkSold() {
            testItem.markSold();
            assertFalse(testItem.isAvailable());
        }

        /**
         * Tests the string representation of the item.
         * Ensures that the toString method returns the correct format.
         */
        @Test
        void testToString() {
            String expected = "Test Item - $30.00 - Sold by: seller123 - Available";
            assertEquals(expected, testItem.toString());
        }

        /**
         * Tests the string representation of the item.
         * Ensures that the toString method returns the correct format.
         */
        @Test
        void testGetters() {
            assertEquals("Test Item", testItem.getName());
            assertEquals(30.0, testItem.getCost());
            assertEquals(seller, testItem.getSoldBy());
            assertTrue(testItem.isAvailable());
            assertEquals("testImage.png", testItem.getImage());
            assertEquals("Test Category", testItem.getCategory());
        }
    }

    /**
     * Tests for Apparel item type specific functionality.
     */
    @Nested
    class ApparelTest {
        private Apparel apparel;
        private MarketplaceUser user;

        /**
         * Sets up the test environment by initializing a user and an apparel item.
         * The apparel item is initialized with default values for testing.
         */
        @BeforeEach
        void setUp() {
            user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            apparel = new Apparel("T-Shirt", 20.0, user, "tshirt.png", "Clothing", "M", "Red", "Nike");
        }

        /**
         * Tests the getter for the size property of the apparel item.
         * Ensures that the correct size is returned.
         */
        @Test
        void testGetSize() {
            assertEquals("M", apparel.getSize());
        }

        /**
         * Tests the getter for the color property of the apparel item.
         * Ensures that the correct color is returned.
         */
        @Test
        void testGetColor() {
            assertEquals("Red", apparel.getColor());
        }

        /**
         * Tests the getter for the brand property of the apparel item.
         * Ensures that the correct brand is returned.
         */
        @Test
        void testGetBrand() {
            assertEquals("Nike", apparel.getBrand());
        }

        /**
         * Tests the setter for the size property of the apparel item.
         * Ensures that the size is updated correctly.
         */
        @Test
        void testSetSize() {
            apparel.setSize("L");
            assertEquals("L", apparel.getSize());
        }

        /**
         * Tests the setter for the color property of the apparel item.
         * Ensures that the color is updated correctly.
         */
        @Test
        void testSetColor() {
            apparel.setColor("Blue");
            assertEquals("Blue", apparel.getColor());
        }

        /**
         * Tests the setter for the brand property of the apparel item.
         * Ensures that the brand is updated correctly.
         */
        @Test
        void testSetBrand() {
            apparel.setBrand("Adidas");
            assertEquals("Adidas", apparel.getBrand());
        }

        /**
         * Tests the string representation of the apparel item.
         * Ensures that the toString method returns the correct format.
         */
        @Test
        void testToString() {
            String expected = 
                "T-Shirt - $20.00 - Sold by: iyoon - Available - " + 
                "Size: M - Color: Red - Brand: Nike";
            assertEquals(expected, apparel.toString());
        }
    }

    /**
     * Tests for Collectible item type specific functionality.
     */
    @Nested
    class CollectibleTest {
        private Collectible collectible;
        private MarketplaceUser user;

        /**
         * Sets up the test environment by initializing a user and an apparel item.
         * The apparel item is initialized with default values for testing.
         */
        @BeforeEach
        void setUp() {
            user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            collectible = new Collectible("Vintage Coin", 150.0, user, "coin.png", "Antiques", "Coin", "Mint");
        }

        /**
         * Tests the getter for the type property of the collectible item.
         * Ensures that the correct type is returned.
         */
        @Test
        void testGetType() {
            assertEquals("Coin", collectible.getType());
        }

        /**
         * Tests the setter for the type property of the collectible item.
         * Ensures that the type is updated correctly.
         */
        @Test
        void testSetType() {
            collectible.setType("Stamp");
            assertEquals("Stamp", collectible.getType());
        }

        /**
         * Tests the getter for the condition property of the collectible item.
         * Ensures that the correct condition is returned.
         */
        @Test
        void testGetCondition() {
            assertEquals("Mint", collectible.getCondition());
        }

        /**
         * Tests the setter for the condition property of the collectible item.
         * Ensures that the condition is updated correctly.
         */
        @Test
        void testSetCondition() {
            collectible.setCondition("Good");
            assertEquals("Good", collectible.getCondition());
        }

        /**
         * Tests the string representation of the collectible item.
         * Ensures that the toString method returns the correct format.
         */
        @Test
        void testToString() {
            String expected = 
                "Vintage Coin - $150.00 - Sold by: iyoon - Available - " +
                "Type: Coin - Condition: Mint";
            assertEquals(expected, collectible.toString());
        }
    }

    /**
     * Tests for Electronic item type specific functionality.
     */
    @Nested
    class ElectronicTest {
        private Electronic electronic;
        private MarketplaceUser user;

        /**
         * Sets up the test environment by initializing a user and an electronic item.
         * The electronic item is initialized with default values for testing.
         */
        @BeforeEach
        void setUp() {
            user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            electronic = new Electronic("Laptop", 800.0, user, "laptop.png", "Electronics", "Apple", 2023);
        }

        /**
         * Tests the getter for the brand property of the electronic item.
         * Ensures that the correct brand is returned.
         */
        @Test
        void testGetType() {
            assertEquals("Apple", electronic.getType());
        }

        /**
         * Tests the getter for the year property of the electronic item.
         * Ensures that the correct year is returned.
         */
        @Test
        void testGetYear() {
            assertEquals(2023, electronic.getYear());
        }

        /**
         * Tests the setter for the type property of the electronic item.
         * Ensures that the type is updated correctly.
         */
        @Test
        void testSetType() {
            electronic.setType("Dell");
            assertEquals("Dell", electronic.getType());
        }

        /**
         * Tests the setter for the year property of the electronic item.
         * Ensures that the year is updated correctly.
         */
        @Test
        void testSetYear() {
            electronic.setYear(2022);
            assertEquals(2022, electronic.getYear());
        }

        /**
         * Tests the string representation of the electronic item.
         * Ensures that the toString method returns the correct format.
         */
        @Test
        void testToString() {
            String expected = " - Brand: Apple - Year: 2023";
            assertEquals(expected, electronic.toString());
        }
    }

    /**
     * Tests for Home item type specific functionality.
     */
    @Nested
    class HomeTest {
        private Home home;
        private MarketplaceUser user;

        /**
         * Sets up the test environment by initializing a user and a home item.
         * The home item is initialized with default values for testing.
         */
        @BeforeEach
        void setUp() {
            user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            home = new Home("Beach Villa", 500000.0, user, "villa.png", "Real Estate", "Mansion");
        }

        /**
         * Tests the getter for the type property of the home item.
         * Ensures that the correct type is returned.
         */
        @Test
        void testGetType() {
            assertEquals("Mansion", home.getType());
        }

        /**
         * Tests the setter for the type property of the home item.
         * Ensures that the type is updated correctly.
         */
        @Test
        void testSetType() {
            home.setType("Apartment");
            assertEquals("Apartment", home.getType());
        }

        /**
         * Tests the string representation of the home item.
         * Ensures that the toString method returns the correct format.
         */
        @Test
        void testToString() {
            String expected = " - Type: Mansion";
            assertEquals(expected, home.toString());
        }

    }

    /**
     * Tests for Vehicle item type specific functionality.
     */
    @Nested
    class VehicleTest {
        private Vehicle vehicle;
        private MarketplaceUser user;

        /**
         * Sets up the test environment by initializing a user and a vehicle item.
         * The vehicle item is initialized with default values for testing.
         */
        @BeforeEach
        void setUp() {
            user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            vehicle = new Vehicle("Tesla Model S", 80000.0, user, "tesla.png", "Vehicles", 238754, 2023, "Tesla");
        }

        /**
         * Tests the getter for the mileage property of the vehicle item.
         * Ensures that the correct mileage is returned.
         */
        @Test
        void testGetMileage() {
            assertEquals(238754, vehicle.getMileage());
        }

        /**
         * Tests the getter for the year property of the vehicle item.
         * Ensures that the correct year is returned.
         */
        @Test
        void testGetYear() {
            assertEquals(2023, vehicle.getYear());
        }

        /**
         * Tests the getter for the brand property of the vehicle item.
         * Ensures that the correct brand is returned.
         */
        @Test
        void testGetBrand() {
            assertEquals("Tesla", vehicle.getBrand());
        }

        /**
         * Tests the setter for the mileage property of the vehicle item.
         * Ensures that the mileage is updated correctly.
         */
        @Test
        void testSetMileage() {
            vehicle.setMileage(300000);
            assertEquals(300000, vehicle.getMileage());
        }

        /**
         * Tests the setter for the year property of the vehicle item.
         * Ensures that the year is updated correctly.
         */
        @Test
        void testSetYear() {
            vehicle.setYear(2022);
            assertEquals(2022, vehicle.getYear());
        }

        /**
         * Tests the setter for the brand property of the vehicle item.
         * Ensures that the brand is updated correctly.
         */
        @Test
        void testSetBrand() {
            vehicle.setBrand("Ford");
            assertEquals("Ford", vehicle.getBrand());
        }

        /**
         * Tests the string representation of the vehicle item.
         * Ensures that the toString method returns the correct format.
         */
        @Test
        void testToString() {
            String expected = " - Mileage: 238754 - Year: 2023 - Brand: Tesla";
            assertEquals(expected, vehicle.toString());
        }
    }
}
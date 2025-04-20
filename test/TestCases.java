package test;

import Service.Marketplace;
import model.items.*;
import model.users.MarketplaceUser;
import model.users.User;
import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
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

        @AfterEach
        void tearDown() {
            File usersFile = new File(USERS_FILE);
            if (usersFile.exists()) {
                usersFile.delete();
            }
            File messagesDir = new File(MESSAGES_DIR);
            if (messagesDir.exists()) {
                for (File file : messagesDir.listFiles()) {
                    file.delete();
                }
                messagesDir.delete();
            }
        }

        @Test
        void testUserCreation() {
            MarketplaceUser user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            assertFalse(user.createNewUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword"));
        }

        @Test
        void testUserCreationWithEmptyFields() {
            MarketplaceUser user = new MarketplaceUser("", "Peter", "peterEmpty", "notAStrongPassword");
            assertFalse(user.createNewUser("", "Peter", "peterEmpty", "notAStrongPassword"));
        }

        @Test
        void testCreateDuplicateUser() {
            MarketplaceUser user1 = new MarketplaceUser("Youssef", "Abdelkader", "youxeff", "pASsWord");

            MarketplaceUser user2 = new MarketplaceUser("Youssef", "Abdelkader", "youxeff", "pASsWord");
            assertFalse(user2.createNewUser("Youssef", "Abdelkader", "youxeff", "pASsWord"));
        }

        @Test
        void testLoginSuccess() {
            MarketplaceUser user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            assertTrue(user.login("iyoon", "thisIsAStrongPassword"));
        }

        @Test
        void testLoginFails() {
            MarketplaceUser user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            assertFalse(user.login("iyoon", "wrongPassword"));
        }

        @Test
        void testSendMessageToUser() {
            MarketplaceUser user1 = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            MarketplaceUser user2 = new MarketplaceUser("Arjun", "Anilkumar", "aanil", "anotherStrongPassword");

            user1.sendMessageTo("aanil", "Hello, Isaac!");
            File messageFile = new File("messages/aanil.txt");
            assertTrue(messageFile.exists());
        }

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

        @Test
        void testUserExists() {
            MarketplaceUser user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            user.createNewUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            assertTrue(MarketplaceUser.userExists("iyoon"));
            assertFalse(MarketplaceUser.userExists("nonExistentUser"));
        }

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

        @BeforeEach
        void setUp() throws IOException {
            new File(USERS_FILE).createNewFile();
            new File(ITEMS_FILE).createNewFile();

            marketplace = new Marketplace();
            testUser = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAPassword");
            marketplace.updateUserData(testUser);
        }

        @AfterEach
        void tearDown() {
            new File(USERS_FILE).delete();
            new File(ITEMS_FILE).delete();
        }

        @Test
        void testUpdateUserData() throws IOException {
            ArrayList<User> users = marketplace.loadAllUsers();
            assertEquals(1, users.size());
            assertEquals("Isaac", users.get(0).getFirstName());
        }

        @Test
        void testSearchSeller() {
            ArrayList<User> searchResults = marketplace.searchSeller("Isaac");
            assertEquals(1, searchResults.size());
            assertEquals("iyoon", searchResults.get(0).getUserName());
        }

        @Test
        void testAuthenticateUser() {
            User authenticatedUser = marketplace.authenticateUser("iyoon", "thisIsAPassword");
            assertNotNull(authenticatedUser);
            assertEquals("Isaac", authenticatedUser.getFirstName());
        }

        @Test
        void testAddItem() {
            AbstractItem item = new Electronic("Laptop", 1000.0, testUser, "laptop.png", "Electronics", "Gaming", 2023);
            marketplace.addItem(item);

            ArrayList<Item> resultsByName = marketplace.searchByName("Laptop");
            assertEquals(1, resultsByName.size());
            assertEquals("Laptop", resultsByName.get(0).getName());
        }

        @Test
        void testSearchByName() {
            AbstractItem item = new Electronic("Laptop", 1000.0, testUser, "laptop.png", "Electronics", "Gaming", 2023);
            marketplace.addItem(item);

            ArrayList<Item> results = marketplace.searchByName("Laptop");
            assertEquals(1, results.size());
            assertEquals("Laptop", results.get(0).getName());
        }

        @Test
        void testSearchByCategory() {
            AbstractItem item = new Electronic("Laptop", 1000.0, testUser, "laptop.png", "Electronics", "Gaming", 2023);
            marketplace.addItem(item);

            ArrayList<Item> results = marketplace.searchByCategory("Electronics");
            assertEquals(1, results.size());
            assertEquals("Electronics", results.get(0).getCategory());
        }

        @Test
        void testGetAvailableItems() {
            AbstractItem item = new Electronic("Laptop", 1000.0, testUser, "laptop.png", "Electronics", "Gaming", 2023);
            marketplace.addItem(item);

            ArrayList<Item> availableItems = marketplace.getAvailableItems();
            assertEquals(1, availableItems.size());
        }

        @Test
        void testPurchaseItem() throws IOException {
            AbstractItem item = new Electronic("Laptop", 1000.0, testUser, "laptop.png", "Electronics", "Gaming", 2023);
            marketplace.addItem(item);

            MarketplaceUser buyer = new MarketplaceUser("John", "Doe", "jdoe", "password456");
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

        private AbstractItem testItem;
        private MarketplaceUser seller;
        private MarketplaceUser buyer;

        @BeforeEach
        void setUp() {
            seller = new MarketplaceUser("Seller", "Test", "seller123", "thisIsAPassword", 100.0, true);
            buyer = new MarketplaceUser("Buyer", "Test", "buyer123", "password", 50.0, true);

            testItem = new AbstractItem("Test Item", 30.0, seller, "testImage.png", "Test Category") {
            };
        }

        @Test
        void testSellItemSuccess() {
            boolean result = testItem.sellItem(buyer);
            assertTrue(result);
            assertEquals(20.0, buyer.getBalance());
            assertEquals(130.0, seller.getBalance());
            assertFalse(testItem.isAvailable());
        }

        @Test
        void testSellItemInsufficientBalance() {
            buyer.setBalance(10.0);
            boolean result = testItem.sellItem(buyer);
            assertFalse(result);
            assertEquals(10.0, buyer.getBalance());
            assertEquals(100.0, seller.getBalance());
            assertTrue(testItem.isAvailable());
        }

        @Test
        void testSellItemAlreadySold() {
            testItem.sellItem(buyer);
            boolean result = testItem.sellItem(buyer);
            assertFalse(result);
        }

        @Test
        void testDeleteItem() {
            boolean result = testItem.deleteItem();
            assertTrue(result);
            assertFalse(testItem.isAvailable());
        }

        @Test
        void testMarkSold() {
            testItem.markSold();
            assertFalse(testItem.isAvailable());
        }

        @Test
        void testToString() {
            String expected = "Test Item - $30.00 - Sold by: seller123 - Available";
            assertEquals(expected, testItem.toString());
        }

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

        @BeforeEach
        void setUp() {
            user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            apparel = new Apparel("T-Shirt", 20.0, user, "tshirt.png", "Clothing", "M", "Red", "Nike");
        }

        @Test
        void testGetSize() {
            assertEquals("M", apparel.getSize());
        }

        @Test
        void testGetColor() {
            assertEquals("Red", apparel.getColor());
        }

        @Test
        void testGetBrand() {
            assertEquals("Nike", apparel.getBrand());
        }

        @Test
        void testSetSize() {
            apparel.setSize("L");
            assertEquals("L", apparel.getSize());
        }

        @Test
        void testSetColor() {
            apparel.setColor("Blue");
            assertEquals("Blue", apparel.getColor());
        }

        @Test
        void testSetBrand() {
            apparel.setBrand("Adidas");
            assertEquals("Adidas", apparel.getBrand());
        }

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

        @BeforeEach
        void setUp() {
            user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            collectible = new Collectible("Vintage Coin", 150.0, user, "coin.png", "Antiques", "Coin", "Mint");
        }

        @Test
        void testGetType() {
            assertEquals("Coin", collectible.getType());
        }

        @Test
        void testSetType() {
            collectible.setType("Stamp");
            assertEquals("Stamp", collectible.getType());
        }

        @Test
        void testGetCondition() {
            assertEquals("Mint", collectible.getCondition());
        }

        @Test
        void testSetCondition() {
            collectible.setCondition("Good");
            assertEquals("Good", collectible.getCondition());
        }

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

        @BeforeEach
        void setUp() {
            user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            electronic = new Electronic("Laptop", 800.0, user, "laptop.png", "Electronics", "Apple", 2023);
        }

        @Test
        void testGetType() {
            assertEquals("Apple", electronic.getType());
        }

        @Test
        void testGetYear() {
            assertEquals(2023, electronic.getYear());
        }

        @Test
        void testSetType() {
            electronic.setType("Dell");
            assertEquals("Dell", electronic.getType());
        }

        @Test
        void testSetYear() {
            electronic.setYear(2022);
            assertEquals(2022, electronic.getYear());
        }

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

        @BeforeEach
        void setUp() {
            user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            home = new Home("Beach Villa", 500000.0, user, "villa.png", "Real Estate", "Mansion");
        }

        @Test
        void testGetType() {
            assertEquals("Mansion", home.getType());
        }

        @Test
        void testSetType() {
            home.setType("Apartment");
            assertEquals("Apartment", home.getType());
        }

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

        @BeforeEach
        void setUp() {
            user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            vehicle = new Vehicle("Tesla Model S", 80000.0, user, "tesla.png", "Vehicles", 238754, 2023, "Tesla");
        }

        @Test
        void testGetMileage() {
            assertEquals(238754, vehicle.getMileage());
        }

        @Test
        void testGetYear() {
            assertEquals(2023, vehicle.getYear());
        }

        @Test
        void testGetBrand() {
            assertEquals("Tesla", vehicle.getBrand());
        }

        @Test
        void testSetMileage() {
            vehicle.setMileage(300000);
            assertEquals(300000, vehicle.getMileage());
        }

        @Test
        void testSetYear() {
            vehicle.setYear(2022);
            assertEquals(2022, vehicle.getYear());
        }

        @Test
        void testSetBrand() {
            vehicle.setBrand("Ford");
            assertEquals("Ford", vehicle.getBrand());
        }

        @Test
        void testToString() {
            String expected = " - Mileage: 238754 - Year: 2023 - Brand: Tesla";
            assertEquals(expected, vehicle.toString());
        }
    }
}
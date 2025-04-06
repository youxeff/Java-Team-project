package test;

import Service.Marketplace;
import model.items.AbstractItem;
import model.users.MarketplaceUser;
import model.users.User;
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.experimental.runners.Enclosed;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;
import org.junit.rules.Timeout;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

//Testing

import org.junit.jupiter.api.*;
import java.io.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

import static org.junit.Assert.*;

public class TestCases {

    @Nested
    public static class MarketplaceUserTest {

        @Test
        public void testUserCreation() {
            MarketplaceUser user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            assertEquals("Isaac", user.getFirstName());
            assertEquals("Yoon", user.getLastName());
            assertEquals("iyoon", user.getUserName());
            assertEquals("thisIsAStrongPassword", user.getPassword());
            assertEquals(0.0, user.getBalance());
        }

        @Test
        public void testEmptyFields() {
            MarketplaceUser user = new MarketplaceUser("", "Peter", "peterEmpty", "notAStrongPassword");
            assertFalse(user.createNewUser("", "Peter", "peterEmpty", "notAStrongPassword"));
        }

        @Test
        public void testDuplicateUsername() {
            MarketplaceUser user1 = new MarketplaceUser("Youssef", "Abdelkader", "youxeff", "pASsWord");
            MarketplaceUser user2 = new MarketplaceUser("Youssef", "Abdelkader", "youxeff", "pASsWord");
            assertFalse(user2.createNewUser("Youssef", "Abdelkader", "youxeff", "pASsWord"));
        }

        @Test
        public void testVerifyCredentials() {
            MarketplaceUser user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            assertTrue(user.verifyCredentials("iyoon", "thisIsAStrongPassword"));
            assertFalse(user.verifyCredentials("iyoon", "wrongPassword"));
        }

        @Test
        public void testLoadUser() {
            MarketplaceUser user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            MarketplaceUser loadedUser = MarketplaceUser.loadUser("iyoon");
            assertNotNull(loadedUser);
            assertEquals("Isaac", loadedUser.getFirstName());
            assertEquals("Yoon", loadedUser.getLastName());
            assertEquals("iyoon", loadedUser.getUserName());
        }
    }

    @Nested
    public static class MarketplaceTest {
        private static final String USERS_FILE = "users.txt";
        private static final String ITEMS_FILE = "items.txt";
        private Marketplace marketplace;
        private MarketplaceUser testUser;

        @Before
        public void setUp() throws IOException {
            new PrintWriter(USERS_FILE).close();
            new PrintWriter(ITEMS_FILE).close();

            marketplace = new Marketplace();
            testUser = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAPassword");
            marketplace.updateUserData(testUser);
        }

        @After
        public void tearDown() throws IOException {
            new File(USERS_FILE).delete();
            new File(ITEMS_FILE).delete();
        }

        @Test
        public void testUpdateUserData() throws IOException {
            ArrayList<User> users = marketplace.loadAllUsers();
            assertEquals(1, users.size());
            assertEquals("Isaac", users.get(0).getFirstName());
        }

        @Test
        public void testSearchSeller() {
            ArrayList<User> searchResults = marketplace.searchSeller("Isaac");
            assertEquals(1, searchResults.size());
            assertEquals("iyoon", searchResults.get(0).getUserName());
        }

        @Test
        public void testAuthenticateUser() {
            User authenticatedUser = marketplace.authenticateUser("iyoon", "thisIsAPassword");
            assertNotNull(authenticatedUser);
            assertEquals("Isaac", authenticatedUser.getFirstName());
        }

        @Test
        public void testAddAndSearchItems() {
            AbstractItem item = new AbstractItem("Computer", 500.0, testUser, "img.png", "Electronics");
            marketplace.addItem(item);

            ArrayList<AbstractItem> resultsByName = marketplace.searchByName("Computer");
            assertEquals(1, resultsByName.size());

            ArrayList<AbstractItem> resultsByCategory = marketplace.searchByCategory("Electronics");
            assertEquals(1, resultsByCategory.size());
        }

        @Test
        public void testGetAvailableItems() {
            AbstractItem item = new AbstractItem("Computer", 500.0, testUser, "img.png", "Electronics");
            marketplace.addItem(item);

            ArrayList<AbstractItem> availableItems = marketplace.getAvailableItems();
            assertEquals(1, availableItems.size());
        }
    }
}
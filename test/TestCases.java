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

import org.junit.jupiter.api.*;
import java.io.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

import static org.junit.Assert.*;
//This class contains all the test cases in order to ensure
//that the code runs properly and checks all possible errors
//throwing exceptions pinpoint where the code breaks
//if it does break.

@RunWith(Enclosed.class)
public class TestCases {

    @Nested
    public static class marketplaceUserTest {
        @Test
        public void testUser() {
            try {
                ArrayList<User> expectedUsers = new ArrayList<>();
                MarketplaceUser user1 = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
                assertEquals("Isaac", user1.getFirstName());
                assertEquals("Yoon", user1.getLastName());
                assertEquals("thisIsAStrongPassword", user1.getPassword());
                assertEquals(0.0, user1.getBalance());
                assertEquals("iyoon", user1.getUserName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Test
        public void emtyFields() {
            MarketplaceUser user = new Marketplace("", "Peter", "peterEmpty", "notAStrongPassword");
            assertFalse(user.createNewUser("", "Peter", "peterEmpty", "notAStrongPassword"));
        }

        @Test
        public void failDuplicateUsername() {
            Marketplace user = new Marketplace("Youssef", "Abdelkader", "youxeff", "pASsWord");
            boolean created = new Marketplace("Youssef", "Abdelkader", "youxeff", "pASsWord");
            assertFalse(created);
        }

        @Test
        public void testVerifyCredentials() {
            MarketplaceUser user = new MarketplaceUser("Isaac", "Yoon", "iyoon",  "thisIsAStrongPassword");
            assertTrue(user.verifyCredentials("iyoon", "thisIsAStrongPassword"));
        }

        @Test
        public void testVerifyCredentialsFail() {
            MarketplaceUser marketplaceUser = new MarketplaceUser(new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword"));
            assertFalse(marketplaceUser.verifyCredentials("iyoon", "wrongPassword"));
        }

        @Test
        public void testLogin() {
            MarketplaceUser user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            assertTrue(user.login("iyoon", "thisIsAStrongPassword"));
            assertFalse(user.login("iyoon", "wrongPassword"));
        }

        @Test
        public void LestLoadUsers() {
            new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            MarketplaceUser loaded = MarketplaceUser.loadUser("iyoon");

            assertNotNull(loaded);
            assertEquals("Isaac", loaded.getFirstName());
            assertEquals("Yoon", loaded.getLastName());
            assertEquals("iyoon",loaded.getUserName());
        }

        @Test
        public void testPassword() {
            MarketplaceUser user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAStrongPassword");
            assertTrue(user.verifyPassword("thisIsAStrongPassword"));
            assertFalse(user.verifyPassword("thisIsAWrongPassword"));
        }
    }

    @Nested
    public class marketplaceTests {
        private static final String USERS_FILE = "users.txt";
        private static final String ITEMS_FILE = "items.txt";
        private Marketplace marketplace;
        private MarketplaceUser testUser;

        @BeforeEach
        void setUp() throws IOException {
            new PrintWriter(USERS_FILE).close();
            new PrintWriter(ITEMS_FILE).close();

            marketplace = new Marketplace();
            user = new MarketplaceUser("Isaac", "Yoon", "iyoon", "thisIsAPassword");
            marketplace.updateUserData(user);
        }

        @Test
        public void testUserDataFile() throws IOException {
            File file = new File(USERS_FILE);
            assertTrue(file.exists());
        }

        @Test
        public void testUpdateUserData() throws IOException {
            ArrayList<User> users = marketplace.loadAllUsers();
            assertEquals(1, users.size());
            assertEquals("Isaac", users.get(0).getFirstName());
        }

        @Test
        public void testSeearch() {
            ArrayList<User> search = marketplace.searchSeller("Isaac");
            assertEquals(1, search.size());
            assertEquals("iyoon", search.get(0).getUserName());
        }

        @Test
        public void testAuthenticateUser() {
            User authenticate = marketplace.authenticateUser("iyoon", "thisIsAPassword");
            assertNotNull(authenticate);
            assertEquals("Isaac", authenticate.getFirstName());
        }

        @Test
        public void testAddAndSearchItems() {
            AbstractItem item = new AbstractItem("Computer", 500.0, user, "img.png", "Electronics");
            marketplace.addItem(item);
            ArrayList<AbstractItem> results = marketplace.searchByName("Computer");
            assertEquals(1, results.size());
            ArrayList<AbstractItem> category = marketplace.searchByCategory("Electronics");
            assertEquals(1, category.size());
        }

        @Test
        public void getAvailableItems() {
            AbstractItem item = new AbstractItem("Computer", 500.0, user, "img.png", "Electronics");
            marketplace.addItem(item);

            ArrayList<AbstractItem> available = marketplace.getAvailableItems();
            assertEquals(1, available.size());
        }
    }

    @Nested
    public class testAbstractItem {
        @BeforeEach
        public void setUp() throws IOException {
            seller = new
        }
    }
}
package model.items;
import java.util.ArrayList;

import model.users.User;

public interface Item {
    // Transaction methods
    Boolean sellItem(User buyer);

    Boolean deleteItem();
    void markSold();

    // Search methods (these might be better in Marketplace)
    ArrayList<Item> searchByName(String name);
    ArrayList<Item> searchByCategory(String category);

    // Display method
    String toString();

    // Getter methods
    String getName();
    double getCost();
    User getSoldBy();
    boolean isAvailable();
    String getImage();
    String getCategory();
}

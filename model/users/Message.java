package model.users;

import java.util.ArrayList;
/**
 * This program creates an Interface for the MarketplaceUser class.
 * This is implemented in the MarketplaceUser class for direct messaging purposes.
 */

public interface Message {
    
    void sendMessageTo(String recipientName, String message);
    ArrayList<String> viewMessages();
}


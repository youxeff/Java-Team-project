package model.users;

import java.util.ArrayList;

/**
 * Defines the messaging capabilities for marketplace users.
 * Allows sending messages to other users and viewing received messages.
 *
 * @author Youssef Abdelkader
 * @author Anthony Kim  
 * @author Caroline Murphy
 * @author Eric Yen
 * @author Isaac Yoon
 * @version April 20 2025
 */
public interface Message {
    
    /**
     * Sends a message to another user.
     * @param recipientName Username of the message recipient
     * @param message Content of the message to send
     */
    void sendMessageTo(String recipientName, String message);
    
    /**
     * Displays all messages received by this user.
     * @return List of messages received by this user
     */
    ArrayList<String> viewMessages();
}

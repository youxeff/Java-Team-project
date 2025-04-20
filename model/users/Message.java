package model.users;

import java.util.ArrayList;
/**
 * Defines the messaging capabilities for marketplace users.
 * Allows sending messages to other users and viewing received messages.
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
     */
    ArrayList<String> viewMessages();
}

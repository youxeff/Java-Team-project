package model.users;
public interface Message {
    
    void sendMessageTo(String recipientName, String message);
    void viewMessages();
}


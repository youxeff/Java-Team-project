package Network;

import java.io.*;
import java.net.*;

/**
 * Client-side interface for marketplace access.
 * Handles user input and server communication with non-blocking I/O.
 * 
 * @author Youssef Abdelkader
 * @author Anthony Kim  
 * @author Caroline Murphy
 * @author Eric Yen
 * @author Isaac Yoon
 * @version April 20 2025
 */
public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to marketplace server.");

            // Create a separate thread to handle server responses
            Thread serverResponseThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    System.err.println("Connection to server lost: " + e.getMessage());
                }
            });
            
            serverResponseThread.start();

            String input;
            while ((input = userInput.readLine()) != null) {
                out.println(input);
                if (input.equalsIgnoreCase("exit")) {
                    break;
                }
            }
            
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}
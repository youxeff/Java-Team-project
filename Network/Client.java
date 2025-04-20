package Network;

import java.io.*;
import java.net.*;

/**
 * Client class that handles user connections to the marketplace server.
 * Manages communication between users and the server through a socket connection.
 */
public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    /**
     * Main entry point for the client application.
     * Establishes connection with server and handles I/O streams.
     * Creates a separate thread for handling server responses.
     *
     * @param args command line arguments (not used)
     */
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
package Network;

import java.io.*;
import java.net.*;

/**
 * Client-side interface for marketplace access.
 * Handles user input and server communication with non-blocking I/O.
 * Manages persistent connection with server and provides clean disconnection.
 * 
 * @author Youssef Abdelkader
 * @author Anthony Kim  
 * @author Caroline Murphy
 * @author Eric Yen
 * @author Isaac Yoon
 * @version April 20 2025
 */
public class Client implements IClient {
    /** Server hostname */
    private static final String HOST = "localhost";
    /** Server port number */
    private static final int PORT = 12345;

    /** Socket connection to server */
    private Socket socket;
    /** Input stream for server messages */
    private BufferedReader in;
    /** Output stream for sending messages to server */
    private PrintWriter out;
    /** Input stream for user input */
    private BufferedReader userInput;
    /** Thread for handling server responses */
    private Thread serverResponseThread;

    /**
     * {@inheritDoc}
     * Establishes connection to server and initializes communication streams.
     * Creates and starts a separate thread for handling server responses.
     */
    @Override
    public void start() {
        try {
            socket = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            userInput = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Connected to marketplace server.");

            serverResponseThread = new Thread(() -> {
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
                send(input);
                if (input.equalsIgnoreCase("exit")) {
                    break;
                }
            }

            disconnect();

        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     * Sends a message to the server if connection is active.
     * Messages are automatically flushed to ensure immediate transmission.
     *
     * @param input the message to send to the server
     */
    @Override
    public void send(String input) {
        if (out != null) {
            out.println(input);
        }
    }

    /**
     * {@inheritDoc}
     * Performs graceful shutdown of client connection.
     * Closes all streams and the socket connection, interrupts response thread.
     */
    @Override
    public void disconnect() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (userInput != null) userInput.close();
            if (socket != null) socket.close();
            if (serverResponseThread != null) serverResponseThread.interrupt();
        } catch (IOException e) {
            System.err.println("Error during disconnect: " + e.getMessage());
        }
    }

    /**
     * Main entry point for the client application.
     * Creates and starts a new client instance.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        IClient client = new Client();
        client.start();
    }
}
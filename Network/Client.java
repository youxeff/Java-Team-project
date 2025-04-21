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

 public class Client implements IClient {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private BufferedReader userInput;
    private Thread serverResponseThread;

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

    @Override
    public void send(String input) {
        if (out != null) {
            out.println(input);
        }
    }

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

    public static void main(String[] args) {
        IClient client = new Client();
        client.start();
    }
}
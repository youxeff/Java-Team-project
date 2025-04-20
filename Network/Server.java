package Network;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import Service.Marketplace;

/**
 * Server class that handles the marketplace's network functionality.
 * Manages client connections and initializes the marketplace service.
 */
public class Server {
    private static final int PORT = 12345;
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private Marketplace marketplace;

    /**
     * Main entry point for the server application.
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        new Server().startServer();
    }

    /**
     * Constructs a new Server instance and initializes the marketplace.
     */
    public Server() {
        // The marketplace is initialized once for the server
        this.marketplace = new Marketplace();
        System.out.println("Marketplace initialized");
    }

    /**
     * Starts the server and begins accepting client connections.
     * Creates a new ClientHandler for each connected client.
     */
    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }
}
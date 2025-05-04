package Network;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import Service.Marketplace;

/**
 * Server implementation for the marketplace system.
 * Handles multiple client connections concurrently using a thread pool.
 * @author Youssef Abdelkader
 * @author Anthony Kim 
 * @author Caroline Murphy
 * @author Eric Yen
 * @author Isaac Yoon
 *
 * @version April 20 2025
 */
public class Server implements IServer {
    /** The port number on which the server listens for connections */
    private static final int PORT = 12345;
    /** Thread pool for handling multiple client connections */
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    /** The marketplace instance shared across all client connections */
    private Marketplace marketplace;
    /** Flag indicating whether the server is currently running */
    private boolean running = true;
    /** The server socket that accepts client connections */
    private ServerSocket serverSocket;

    /**
     * Main entry point for the server application.
     * Creates and starts a new server instance.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        IServer server = new Server();
        server.startServer();
    }

    /**
     * Constructs a new Server instance.
     * Initializes the marketplace that will be shared across all clients.
     */
    public Server() {
        this.marketplace = new Marketplace();
        System.out.println("Marketplace initialized");
    }

    /**
     * {@inheritDoc}
     * Creates a server socket and begins accepting client connections.
     * Each client connection is handled by a new ClientHandler in the thread pool.
     */
    @Override
    public void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("Server error: " + e.getMessage());
            }
        } finally {
            stopServer();
        }
    }

    /**
     * {@inheritDoc}
     * Cleanly shuts down the server, closing all connections and the thread pool.
     */
    @Override
    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }
}

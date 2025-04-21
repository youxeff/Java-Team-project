package Network;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import Service.Marketplace;

/**
 * Core server implementation for the marketplace system.
 * Handles client connections with a multithreaded design and shared marketplace instance.
 *
 * @author Youssef Abdelkader
 * @author Anthony Kim  
 * @author Caroline Murphy
 * @author Eric Yen
 * @author Isaac Yoon
 * @version April 20 2025
 */
public class Server {
    private static final int PORT = 12345;
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private Marketplace marketplace;

    public static void main(String[] args) {
        new Server().startServer();
    }

    public Server() {
        // The marketplace is initialized once for the server
        this.marketplace = new Marketplace();
        System.out.println("Marketplace initialized");
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println(
                    "New client connected: " + 
                    clientSocket.getInetAddress().getHostAddress());
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }
}
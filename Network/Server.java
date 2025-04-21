package Network;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import Service.Marketplace;

public class Server implements IServer {
    private static final int PORT = 12345;
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private Marketplace marketplace;
    private boolean running = true;
    private ServerSocket serverSocket;

    public static void main(String[] args) {
        IServer server = new Server();
        server.startServer();
    }

    public Server() {
        this.marketplace = new Marketplace();
        System.out.println("Marketplace initialized");
    }

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

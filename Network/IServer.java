package Network;

/**
 * Interface defining core server functionality for the marketplace system.
 * 
 * @author Youssef Abdelkader
 * @author Anthony Kim  
 * @author Caroline Murphy
 * @author Eric Yen
 * @author Isaac Yoon
 * @version April 20 2025
 */
public interface IServer {
    /**
     * Starts the server and begins listening for client connections.
     * Creates a new thread for each connected client.
     */
    void startServer();

    /**
     * Stops the server gracefully, closing all client connections
     * and cleaning up resources.
     */
    void stopServer();
}
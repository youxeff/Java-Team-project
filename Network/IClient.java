package Network;

/**
 * Interface defining core client functionality for the marketplace system.
 * Handles client-side connection and communication with the server.
 * 
 * @version April 20 2025
 */
public interface IClient {
    /**
     * Initializes and starts the client connection to the server.
     * Sets up input/output streams and begins message handling.
     */
    void start();

    /**
     * Sends a message to the server.
     * @param input The message to send to the server
     */
    void send(String input);

    /**
     * Closes the client connection and cleans up resources.
     * Properly terminates all streams and socket connections.
     */
    void disconnect();
}

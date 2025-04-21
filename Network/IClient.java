package Network;

public interface IClient {

    void start();

    void send(String input);

    void disconnect();
}

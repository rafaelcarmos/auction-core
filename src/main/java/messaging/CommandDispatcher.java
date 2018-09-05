package messaging;

public interface CommandDispatcher {

    void processCommand(String rawMessage) throws Exception;

    void shutdown();
}

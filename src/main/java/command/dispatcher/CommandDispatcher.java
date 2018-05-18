package command.dispatcher;

import com.google.gson.JsonObject;

public interface CommandDispatcher {

    void processCommand(JsonObject rawMessage) throws Exception;

    void shutdown();
}

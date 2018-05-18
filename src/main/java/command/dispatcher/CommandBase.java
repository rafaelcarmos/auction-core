package command.dispatcher;

import com.google.gson.JsonObject;
import command.commands.Command;

import java.time.LocalDateTime;

public class CommandBase {

    private LocalDateTime timestamp;
    private Command command;
    private JsonObject rawMessage;

    public CommandBase() {

    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public JsonObject getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(JsonObject rawMessage) {
        this.rawMessage = rawMessage;
    }
}

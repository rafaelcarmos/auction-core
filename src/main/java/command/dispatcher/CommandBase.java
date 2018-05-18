package command.dispatcher;

import command.commands.Command;

import java.time.LocalDateTime;

public class CommandBase {

    private LocalDateTime timestamp;
    private Command command;
    private String rawMessage;

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

    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }
}

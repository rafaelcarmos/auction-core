package messaging;

import domain.auction.commands.Command;

public class CommandBase {

    private String rawMessage;
    private Command command;

    public CommandBase() {

    }

    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

}

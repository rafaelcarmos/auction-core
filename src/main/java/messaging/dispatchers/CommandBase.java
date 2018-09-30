package messaging.dispatchers;

import domain.auction.commands.Command;

public class CommandBase {

    private byte[] rawMessage;
    private Command command;

    public CommandBase() {

    }

    public byte[] getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(byte[] rawMessage) {
        this.rawMessage = rawMessage;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

}

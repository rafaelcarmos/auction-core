package messaging.dispatchers;

import domain.auction.commands.Command;

import java.nio.ByteBuffer;

public class CommandBase {

    private ByteBuffer rawMessage;
    private Command command;

    public CommandBase() {
        rawMessage = ByteBuffer.allocate(256);
    }

    public ByteBuffer getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(byte[] rawMessage) {
        this.rawMessage.clear();
        this.rawMessage.put(rawMessage);
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

}

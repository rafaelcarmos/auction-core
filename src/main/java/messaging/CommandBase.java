package messaging;

import domain.auction.commands.Command;

public class CommandBase {

    private long sequence;
    private String rawMessage;
    private Command command;

    public CommandBase() {

    }


    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
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

package test;

import command.commands.Command;

import java.time.LocalDateTime;
import java.util.UUID;

public class CallbackCommand extends Command {

    public CallbackCommand(UUID auctionId, LocalDateTime timestamp) {
        super(auctionId, timestamp);
    }
}

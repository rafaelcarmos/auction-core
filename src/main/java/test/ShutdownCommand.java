package test;

import command.commands.Command;

import java.time.LocalDateTime;
import java.util.UUID;

public class ShutdownCommand extends Command {

    public ShutdownCommand(UUID auctionId, LocalDateTime timestamp) {
        super(auctionId, timestamp);
    }
}

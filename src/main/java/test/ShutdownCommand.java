package test;

import domain.auction.commands.Command;

public class ShutdownCommand extends Command {

    public ShutdownCommand(long auctionId, long timestamp) {
        super(auctionId, timestamp);
    }
}

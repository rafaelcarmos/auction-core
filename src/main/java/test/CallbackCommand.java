package test;

import domain.auction.commands.Command;

public class CallbackCommand extends Command {

    public CallbackCommand(long auctionId, long timestamp) {
        super(auctionId, timestamp);
    }
}

package test;

import domain.auction.Auction;
import domain.auction.commands.Command;
import domain.auction.events.Event;

public class CallbackCommand extends Command {

    public CallbackCommand(long auctionId, long timestamp) {
        super(auctionId, timestamp);
    }

    @Override
    public Event accept(Auction auction) {
        return null;
    }
}

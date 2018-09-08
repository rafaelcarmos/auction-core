package test;

import domain.auction.Auction;
import domain.auction.commands.Command;
import domain.auction.events.Event;

public class ShutdownCommand extends Command {

    public ShutdownCommand(long auctionId, long timestamp) {
        super(auctionId, timestamp);
    }

    @Override
    public Event accept(Auction auction) {
        this.notifyAll();
        return null;
    }
}

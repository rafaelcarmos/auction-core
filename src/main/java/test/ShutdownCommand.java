package test;

import domain.auction.Auction;
import domain.auction.commands.Command;
import domain.auction.events.Event;

import java.time.LocalDateTime;
import java.util.UUID;

public class ShutdownCommand extends Command {

    public ShutdownCommand(UUID auctionId, LocalDateTime timestamp) {
        super(auctionId, timestamp);
    }

    @Override
    public Event accept(Auction auction) {
        this.notifyAll();
        return null;
    }
}

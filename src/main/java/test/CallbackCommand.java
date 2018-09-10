package test;

import domain.auction.Auction;
import domain.auction.commands.Command;
import domain.auction.events.Event;

import java.time.LocalDateTime;
import java.util.UUID;

public class CallbackCommand extends Command {

    public CallbackCommand(UUID auctionId, LocalDateTime timestamp) {
        super(auctionId, timestamp);
    }

    @Override
    public Event accept(Auction auction) {
        return null;
    }
}

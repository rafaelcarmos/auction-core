package domain.auction.commands;

import domain.auction.Auction;
import domain.auction.events.Event;

import java.time.LocalDateTime;
import java.util.UUID;

public class FinishAuction extends Command {

    public FinishAuction(UUID auctionId, LocalDateTime timestamp) {
        super(auctionId, timestamp);
    }

    @Override
    public Event accept(Auction auction) throws Exception {
        return auction.onCommand(this);
    }
}

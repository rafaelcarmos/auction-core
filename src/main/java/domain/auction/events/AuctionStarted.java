package domain.auction.events;

import domain.auction.Auction;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuctionStarted extends Event {

    public AuctionStarted(UUID auctionId, LocalDateTime timestamp) {
        super(auctionId, timestamp);
    }

    @Override
    public void accept(Auction auction) {
        auction.onEvent(this);
    }
}

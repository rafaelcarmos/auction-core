package domain.auction.events;

import domain.auction.Auction;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuctionCancelled extends Event {

    public AuctionCancelled(UUID auctionId, LocalDateTime timestamp) {
        super(auctionId, timestamp);
    }

    @Override
    public void accept(Auction auction) {
        auction.onEvent(this);
    }

}

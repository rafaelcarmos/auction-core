package domain.auction.events;

import domain.auction.Auction;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuctionCreated extends Event {
    private final long auctioneerId;
    private final long itemId;

    public AuctionCreated(UUID auctionId, LocalDateTime timestamp, long auctioneerId, long itemId) {
        super(auctionId, timestamp);
        this.auctioneerId = auctioneerId;
        this.itemId = itemId;
    }

    public final long getAuctioneerId() {
        return auctioneerId;
    }

    public final long getItemId() {
        return itemId;
    }


    @Override
    public void accept(Auction auction) {
        auction.onEvent(this);
    }
}

package domain.auction.events;

import domain.auction.Auction;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Event {
    private final UUID auctionId;
    private final LocalDateTime timestamp;

    public Event(UUID auctionId, LocalDateTime timestamp) {
        this.auctionId = auctionId;
        this.timestamp = timestamp;
    }

    public final UUID getAuctionId() {
        return auctionId;
    }

    public final LocalDateTime getTimestamp() {
        return timestamp;
    }

    public abstract void accept(Auction auction);
}

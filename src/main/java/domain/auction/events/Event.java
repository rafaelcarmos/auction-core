package domain.auction.events;

import domain.auction.Auction;

public abstract class Event {
    private final String auctionId;
    private final long timestamp;

    public Event(String auctionId, long timestamp) {
        this.auctionId = auctionId;
        this.timestamp = timestamp;
    }

    public final String getAuctionId() {
        return auctionId;
    }

    public final long getTimestamp() {
        return timestamp;
    }

    public abstract void accept(Auction auction);
}

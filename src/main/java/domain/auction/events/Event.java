package domain.auction.events;

import domain.auction.Auction;

public abstract class Event {
    private final long auctionId;
    private final long timestampMillis;

    public Event(long auctionId, long timestampMillis) {
        this.auctionId = auctionId;
        this.timestampMillis = timestampMillis;
    }

    public final long getAuctionId() {
        return auctionId;
    }

    public final long getTimestampMillis() {
        return timestampMillis;
    }

    public abstract void accept(Auction auction);
}

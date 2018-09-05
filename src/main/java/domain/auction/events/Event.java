package domain.auction.events;

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

    protected abstract String Serialize();

    public String GetSerialized() {
        return String.format("%d|%s", timestampMillis, Serialize());
    }
}

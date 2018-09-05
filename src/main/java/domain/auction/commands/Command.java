package domain.auction.commands;

public abstract class Command {

    private final long auctionId;
    private final long timestamp;

    public Command(long auctionId, long timestamp) {
        this.auctionId = auctionId;
        this.timestamp = timestamp;
    }

    public final long getAuctionId() {
        return auctionId;
    }

    public final long getTimestamp() {
        return timestamp;
    }
}

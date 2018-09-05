package domain.auction.events;

import org.bson.Document;

import java.util.UUID;

public class BidPlaced extends Event {
    private final UUID bidderId;
    private final double amount;

    public BidPlaced(long auctionId, long timestamp, UUID bidderId, double amount) {
        super(auctionId, timestamp);
        this.bidderId = bidderId;
        this.amount = amount;
    }

    public UUID getBidderId() {
        return bidderId;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public Document getEventDataDocument() {
        return new Document()
                .append("bidderId", bidderId.toString())
                .append("amount", amount);
    }
}

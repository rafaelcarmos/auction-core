package domain.auction.events;

import org.bson.Document;

public class AuctionStarted extends Event {

    public AuctionStarted(long auctionId, long timestamp) {
        super(auctionId, timestamp);
    }

    @Override
    public Document getEventDataDocument() {
        return new Document();
    }
}

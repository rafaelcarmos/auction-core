package domain.auction.events;

import org.bson.Document;

public class AuctionEnded extends Event {

    public AuctionEnded(long auctionId, long timestamp) {
        super(auctionId, timestamp);
    }

    @Override
    public Document getEventDataDocument() {
        return new Document();
    }
}

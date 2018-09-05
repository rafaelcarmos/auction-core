package domain.auction.events;

import org.bson.Document;

public class AuctionCancelled extends Event {

    public AuctionCancelled(long auctionId, long timestamp) {
        super(auctionId, timestamp);
    }

    @Override
    public Document getEventDataDocument() {
        return new Document();
    }
}

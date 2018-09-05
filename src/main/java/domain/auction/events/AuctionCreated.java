package domain.auction.events;

import org.bson.Document;

import java.util.UUID;

public class AuctionCreated extends Event {
    private final UUID auctioneerId;
    private final UUID itemId;

    public AuctionCreated(long auctionId, long timestamp, UUID auctioneerId, UUID itemId) {
        super(auctionId, timestamp);
        this.auctioneerId = auctioneerId;
        this.itemId = itemId;
    }

    public final UUID getAuctioneerId() {
        return auctioneerId;
    }

    public final UUID getItemId() {
        return itemId;
    }

    @Override
    public Document getEventDataDocument() {
        return new Document()
                .append("auctioneerId", auctioneerId.toString())
                .append("itemId", itemId.toString());
    }
}

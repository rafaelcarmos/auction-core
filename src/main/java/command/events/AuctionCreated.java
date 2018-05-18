package command.events;

import org.bson.Document;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuctionCreated extends Event {
    private final UUID auctioneerId;
    private final UUID itemId;
    private final double startPrice;

    public AuctionCreated(UUID auctionId, LocalDateTime timestamp, UUID auctioneerId, UUID itemId, double startPrice) {
        super(auctionId, timestamp);
        this.auctioneerId = auctioneerId;
        this.itemId = itemId;
        this.startPrice = startPrice;
    }

    public final UUID getAuctioneerId() {
        return auctioneerId;
    }

    public final UUID getItemId() {
        return itemId;
    }

    public double getStartPrice() {
        return startPrice;
    }

    @Override
    public Document getEventDataDocument() {
        return new Document()
                .append("auctioneerId", auctioneerId.toString())
                .append("itemId", itemId.toString())
                .append("startPrice", startPrice);
    }
}

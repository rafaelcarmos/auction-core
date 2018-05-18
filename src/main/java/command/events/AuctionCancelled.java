package command.events;

import org.bson.Document;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuctionCancelled extends Event {

    public AuctionCancelled(UUID auctionId, LocalDateTime timestamp) {
        super(auctionId, timestamp);
    }

    @Override
    public Document getEventDataDocument() {
        return new Document();
    }
}

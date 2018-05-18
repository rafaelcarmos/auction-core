package command.events;

import org.bson.Document;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuctionEnded extends Event {

    public AuctionEnded(UUID auctionId, LocalDateTime timestamp) {
        super(auctionId, timestamp);
    }

    @Override
    public Document getEventDataDocument() {
        return new Document();
    }
}

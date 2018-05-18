package command.events;

import org.bson.Document;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Event {
    private final UUID auctionId;
    private final LocalDateTime timestamp;

    public Event(UUID auctionId, LocalDateTime timestamp) {
        this.auctionId = auctionId;
        this.timestamp = timestamp;
    }

    public final UUID getAuctionId() {
        return auctionId;
    }

    public final LocalDateTime getTimestamp() {
        return timestamp;
    }

    public abstract Document getEventDataDocument();

    public final Document getDocument() {
        return new Document()
                .append("aggregateId", auctionId.toString())
                .append("aggregateType", "Auction")
                .append("timestamp", timestamp)
                .append("eventType", this.getClass().getSimpleName())
                .append("eventData", getEventDataDocument());
    }
}

package command.commands;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Command {
    private final UUID auctionId;
    private final LocalDateTime timestamp;

    public Command(UUID auctionId, LocalDateTime timestamp) {
        this.auctionId = auctionId;
        this.timestamp = timestamp;
    }

    public final UUID getAuctionId() {
        return auctionId;
    }

    public final LocalDateTime getTimestamp() {
        return timestamp;
    }
}

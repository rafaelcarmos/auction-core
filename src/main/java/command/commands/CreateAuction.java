package command.commands;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreateAuction extends Command {
    private final UUID auctioneerId;
    private final UUID itemId;

    public CreateAuction(UUID auctionId, LocalDateTime timestamp, UUID auctioneerId, UUID itemId) {
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
}

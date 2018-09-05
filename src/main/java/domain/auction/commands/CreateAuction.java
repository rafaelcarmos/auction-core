package domain.auction.commands;

import java.util.UUID;

public class CreateAuction extends Command {
    private final UUID auctioneerId;
    private final UUID itemId;

    public CreateAuction(long auctionId, long timestamp, UUID auctioneerId, UUID itemId) {
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

package domain.auction.commands;

import domain.auction.Auction;
import domain.auction.events.Event;

public class CreateAuction extends Command {
    private final long auctioneerId;
    private final long itemId;

    public CreateAuction(long auctionId, long timestamp, long auctioneerId, long itemId) {
        super(auctionId, timestamp);
        this.auctioneerId = auctioneerId;
        this.itemId = itemId;
    }

    public final long getAuctioneerId() {
        return auctioneerId;
    }

    public final long getItemId() {
        return itemId;
    }

    @Override
    public Event accept(Auction auction) {
        return auction.onCommand(this);
    }
}

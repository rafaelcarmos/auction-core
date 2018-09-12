package domain.auction.events;

import domain.auction.Auction;

public class AuctionCreated extends Event {
    private final long auctioneerId;
    private final long itemId;

    public AuctionCreated(String auctionId, long timestamp, long auctioneerId, long itemId) {
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
    public void accept(Auction auction) {
        auction.onEvent(this);
    }
}

package domain.auction.events;

import domain.auction.Auction;

public class AuctionStarted extends Event {

    public AuctionStarted(String auctionId, long timestamp) {
        super(auctionId, timestamp);
    }

    @Override
    public void accept(Auction auction) {
        auction.onEvent(this);
    }
}

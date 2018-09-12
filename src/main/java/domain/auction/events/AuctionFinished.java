package domain.auction.events;

import domain.auction.Auction;

public class AuctionFinished extends Event {

    public AuctionFinished(String auctionId, long timestamp) {
        super(auctionId, timestamp);
    }

    @Override
    public void accept(Auction auction) {
        auction.onEvent(this);
    }
}

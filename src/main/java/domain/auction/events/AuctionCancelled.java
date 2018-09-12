package domain.auction.events;

import domain.auction.Auction;

public class AuctionCancelled extends Event {

    public AuctionCancelled(String auctionId, long timestamp) {
        super(auctionId, timestamp);
    }

    @Override
    public void accept(Auction auction) {
        auction.onEvent(this);
    }

}

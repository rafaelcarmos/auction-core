package domain.auction.events;

import domain.auction.Auction;

public class BidPlaced extends Event {
    private final long bidderId;
    private final double amount;

    public BidPlaced(String auctionId, long timestamp, long bidderId, double amount) {
        super(auctionId, timestamp);
        this.bidderId = bidderId;
        this.amount = amount;
    }

    public long getBidderId() {
        return bidderId;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public void accept(Auction auction) {
        auction.onEvent(this);
    }
}

package domain.auction.commands;

import domain.auction.Auction;
import domain.auction.events.Event;

public class PlaceBid extends Command {
    private final long bidderId;
    private final double amount;

    public PlaceBid(long auctionId, long timestamp, long bidderId, double amount) {
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
    public Event accept(Auction auction) throws Exception {
        return auction.onCommand(this);
    }
}

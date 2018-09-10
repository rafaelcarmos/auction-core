package domain.auction.events;

import domain.auction.Auction;

import java.time.LocalDateTime;
import java.util.UUID;

public class BidPlaced extends Event {
    private final long bidderId;
    private final double amount;

    public BidPlaced(UUID auctionId, LocalDateTime timestamp, long bidderId, double amount) {
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

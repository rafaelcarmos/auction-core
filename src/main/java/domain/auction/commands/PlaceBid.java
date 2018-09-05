package domain.auction.commands;

import java.util.UUID;

public class PlaceBid extends Command {
    private final UUID bidderId;
    private final double amount;

    public PlaceBid(long auctionId, long timestamp, UUID bidderId, double amount) {
        super(auctionId, timestamp);
        this.bidderId = bidderId;
        this.amount = amount;
    }

    public UUID getBidderId() {
        return bidderId;
    }

    public double getAmount() {
        return amount;
    }
}

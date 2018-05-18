package command.commands;

import java.time.LocalDateTime;
import java.util.UUID;

public class PlaceBid extends Command {
    private final UUID bidderId;
    private final double amount;

    public PlaceBid(UUID auctionId, LocalDateTime timestamp, UUID bidderId, double amount) {
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

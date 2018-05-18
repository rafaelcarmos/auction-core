package query.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Bid {
    private LocalDateTime timestamp;
    private double amount;
    private UUID bidderId;

    public Bid(LocalDateTime timestamp, double amount, UUID bidderId) {
        this.timestamp = timestamp;
        this.amount = amount;
        this.bidderId = bidderId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public double getAmount() {
        return amount;
    }

    public UUID getBidderId() {
        return bidderId;
    }
}

package query.model;

import java.util.List;
import java.util.UUID;

public class Auction {

    private final UUID id;
    private UUID auctioneerId;
    private UUID itemId;
    private double startPrice;
    private UUID currentWinnerId;
    private double currentWinningBid = 0;
    private AuctionState state;
    private List<Bid> bids;

    public Auction(UUID id) {
        this.id = id;
    }



    public enum AuctionState {
        CREATED,
        STARTED,
        CANCELLED,
        ENDED
    }
}

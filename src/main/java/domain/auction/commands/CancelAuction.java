package domain.auction.commands;

public class CancelAuction extends Command {

    public CancelAuction(long auctionId, long timestamp) {
        super(auctionId, timestamp);
    }
}

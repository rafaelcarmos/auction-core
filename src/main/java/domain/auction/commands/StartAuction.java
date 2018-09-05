package domain.auction.commands;

public class StartAuction extends Command {

    public StartAuction(long auctionId, long timestamp) {
        super(auctionId, timestamp);
    }
}

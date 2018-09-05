package domain.auction.commands;

public class EndAuction extends Command {

    public EndAuction(long auctionId, long timestamp) {
        super(auctionId, timestamp);
    }
}

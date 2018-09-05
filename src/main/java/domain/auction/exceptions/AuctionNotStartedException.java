package domain.auction.exceptions;

public class AuctionNotStartedException extends Exception {
    public AuctionNotStartedException(String message) {
        super(message);
    }
}

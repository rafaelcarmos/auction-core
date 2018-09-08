package domain.auction.commands;

import domain.auction.Auction;
import domain.auction.events.Event;

public abstract class Command {

    private final long auctionId;
    private final long timestamp;

    public Command(long auctionId, long timestamp) {
        this.auctionId = auctionId;
        this.timestamp = timestamp;
    }

    public final static Command fromCSV(String csv, long sequence) {

        String[] fields = csv.split(";");

        CommandType commandType = CommandType.fromInt(Integer.parseInt(fields[0]));

        Command command = null;

        switch (commandType) {

            case CREATE_AUCTION:
                command = new CreateAuction(sequence, -1, -1, -1);
                break;
            case CANCEL_AUCTION:
                long auctionIdCancel = Long.parseLong(fields[1]);
                command = new CancelAuction(auctionIdCancel, -1);
                break;
            case START_AUCTION:
                long auctionIdStart = Long.parseLong(fields[1]);
                command = new StartAuction(auctionIdStart, -1);
                break;
            case FINISH_AUCTION:
                long auctionIdFinish = Long.parseLong(fields[1]);
                command = new FinishAuction(auctionIdFinish, -1);
                break;
            case PLACE_BID:
                long auctionIdPlaceBid = Long.parseLong(fields[1]);
                long bidderId = Long.parseLong(fields[2]);
                double bidAmount = Long.parseLong(fields[3]);
                command = new PlaceBid(auctionIdPlaceBid, -1, bidderId, bidAmount);
                break;
        }

        return command;
    }

    public final long getAuctionId() {
        return auctionId;
    }

    public final long getTimestamp() {
        return timestamp;
    }

    public abstract Event accept(Auction auction) throws Exception;
}

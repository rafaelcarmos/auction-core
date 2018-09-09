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

    public final static Command fromCSV(String csv, long sequence, long timestamp) {

        String[] fields = csv.split(";");

        CommandType commandType = CommandType.fromInt(Integer.parseInt(fields[0]));

        int currentIndex = 1;

        if (timestamp == -1) {
            timestamp = Long.parseLong(fields[currentIndex++]);
        }


        Command command = null;

        switch (commandType) {

            case CREATE_AUCTION:
                long auctioneerId = Long.parseLong(fields[currentIndex++]);
                long itemId = Long.parseLong(fields[currentIndex++]);
                command = new CreateAuction(sequence, timestamp, auctioneerId, itemId);
                break;

            case CANCEL_AUCTION:
                long auctionIdCancel = Long.parseLong(fields[currentIndex++]);
                command = new CancelAuction(auctionIdCancel, timestamp);
                break;

            case START_AUCTION:
                long auctionIdStart = Long.parseLong(fields[currentIndex++]);
                command = new StartAuction(auctionIdStart, timestamp);
                break;

            case FINISH_AUCTION:
                long auctionIdFinish = Long.parseLong(fields[currentIndex++]);
                command = new FinishAuction(auctionIdFinish, timestamp);
                break;

            case PLACE_BID:
                long auctionIdPlaceBid = Long.parseLong(fields[currentIndex++]);
                long bidderId = Long.parseLong(fields[currentIndex++]);
                double bidAmount = Long.parseLong(fields[currentIndex++]);
                command = new PlaceBid(auctionIdPlaceBid, timestamp, bidderId, bidAmount);
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

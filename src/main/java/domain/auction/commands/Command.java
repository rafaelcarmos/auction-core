package domain.auction.commands;

import domain.auction.Auction;
import domain.auction.events.Event;

import java.nio.ByteBuffer;

public abstract class Command {

    private final String auctionId;
    private final long timestamp;

    public Command(String auctionId, long timestamp) {
        this.auctionId = auctionId;
        this.timestamp = timestamp;
    }

    public static Command fromCSV(ByteBuffer csv, long timestamp) {

        String[] fields = new String(csv.array()).trim().split(";");

        int currentIndex = 0;
        String auctionId;

        CommandType commandType = CommandType.valueOf(fields[currentIndex++]);

        if (timestamp == -1)
            timestamp = System.currentTimeMillis();

        auctionId = fields[currentIndex++];

        Command command = null;

        switch (commandType) {

            case CREATE_AUCTION:
                long auctioneerId = Long.valueOf(fields[currentIndex++]);
                long itemId = Long.valueOf(fields[currentIndex++]);
                command = new CreateAuction(auctionId, timestamp, auctioneerId, itemId);
                break;

            case CANCEL_AUCTION:
                command = new CancelAuction(auctionId, timestamp);
                break;

            case START_AUCTION:
                command = new StartAuction(auctionId, timestamp);
                break;

            case FINISH_AUCTION:
                command = new FinishAuction(auctionId, timestamp);
                break;

            case PLACE_BID:
                long bidderId = Long.valueOf(fields[currentIndex++]);
                double bidAmount = Double.valueOf(fields[currentIndex++]);
                command = new PlaceBid(auctionId, timestamp, bidderId, bidAmount);
                break;
        }

        return command;
    }

    public final String getAuctionId() {
        return auctionId;
    }

    public final long getTimestamp() {
        return timestamp;
    }

    public abstract Event accept(Auction auction) throws Exception;
}

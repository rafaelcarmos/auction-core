package domain.auction.commands;

import domain.auction.Auction;
import domain.auction.events.Event;
import test.CallbackCommand;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Command {

    private final UUID auctionId;
    private final LocalDateTime timestamp;

    public Command(UUID auctionId, LocalDateTime timestamp) {
        this.auctionId = auctionId;
        this.timestamp = timestamp;
    }

    public final static Command fromCSV(String csv, LocalDateTime timestamp) {

        String[] fields = csv.split(";");

        int currentIndex = 0;
        UUID auctionId = null;

        CommandType commandType = CommandType.valueOf(fields[currentIndex++]);

        if (commandType != CommandType.CALLBACK_COMMAND) {
            if (timestamp == null)
                timestamp = LocalDateTime.parse(fields[currentIndex++]);

            auctionId = UUID.fromString(fields[currentIndex++]);
        }

        Command command = null;

        switch (commandType) {

            case CREATE_AUCTION:
                long auctioneerId = Long.parseLong(fields[currentIndex++]);
                long itemId = Long.parseLong(fields[currentIndex++]);
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
                long bidderId = Long.parseLong(fields[currentIndex++]);
                double bidAmount = Double.parseDouble(fields[currentIndex++]);
                command = new PlaceBid(auctionId, timestamp, bidderId, bidAmount);
                break;

            case CALLBACK_COMMAND:
                synchronized (csv) {
                    csv.notifyAll();
                }
                command = new CallbackCommand(auctionId, timestamp);
                break;
        }

        return command;
    }

    public final UUID getAuctionId() {
        return auctionId;
    }

    public final LocalDateTime getTimestamp() {
        return timestamp;
    }

    public abstract Event accept(Auction auction) throws Exception;
}

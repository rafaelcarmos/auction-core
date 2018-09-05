package messaging.handlers;

import com.lmax.disruptor.EventHandler;
import command.service.AuctionService;
import domain.Repository;
import domain.auction.commands.*;
import messaging.CommandBase;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommandJournaler implements EventHandler<CommandBase> {

    private final Repository repository;
    private final AuctionService auctionService;

    public CommandJournaler(Repository repository, AuctionService auctionService) {
        this.repository = repository;
        this.auctionService = auctionService;
    }

    @Override
    public void onEvent(CommandBase commandBase, long l, boolean b) {
        try {
            String json = commandBase.getRawMessage();
            String commandType = json.get("commandType").getAsString();
            long auctionId = UUID.fromString(json.get("auctionId").getAsString());
            long timestamp = LocalDateTime.now();
            commandBase.setTimestamp(timestamp);
            Command command = null;

            switch (commandType) {
                case "CancelAuction": {
                    command = new CancelAuction(auctionId, timestamp);
                    break;
                }
                case "CreateAuction": {
                    UUID auctioneerId = UUID.fromString(json.get("auctioneerId").getAsString());
                    UUID itemId = UUID.fromString(json.get("itemId").getAsString());
                    double startPrice = json.get("startPrice").getAsDouble();
                    command = new CreateAuction(auctionId, timestamp, auctioneerId, itemId);
                    break;
                }
                case "EndAuction": {
                    command = new EndAuction(auctionId, timestamp);
                    break;
                }
                case "StartAuction": {
                    command = new StartAuction(auctionId, timestamp);
                    break;
                }
                case "PlaceBid": {
                    UUID bidderId = UUID.fromString(json.get("bidderId").getAsString());
                    double amount = json.get("amount").getAsDouble();
                    command = new PlaceBid(auctionId, timestamp, bidderId, amount);
                    break;
                }
                case "CallbackCommand": {
                    synchronized (json) {
                        json.notifyAll();
                    }
                    return;
                }
                default:
                    throw new RuntimeException("Invalid command type :" + commandType);
            }

            commandBase.setCommand(command);
            auctionService.processCommand(command);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(commandBase.getRawMessage());
        }
    }
}

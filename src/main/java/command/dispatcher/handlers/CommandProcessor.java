package command.dispatcher.handlers;

import com.google.gson.JsonObject;
import com.lmax.disruptor.EventHandler;
import command.commands.*;
import command.dispatcher.CommandBase;
import command.repository.Repository;
import command.service.AuctionService;
import test.CallbackCommand;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommandProcessor implements EventHandler<CommandBase> {

    private final Repository repository;
    private final AuctionService auctionService;

    public CommandProcessor(Repository repository, AuctionService auctionService) {
        this.repository = repository;
        this.auctionService = auctionService;
    }

    @Override
    public void onEvent(CommandBase commandBase, long l, boolean b) {
        try {
            JsonObject json = commandBase.getRawMessage();
            String commandType = json.get("commandType").getAsString();
            UUID auctionId = UUID.fromString(json.get("auctionId").getAsString());
            LocalDateTime timestamp = LocalDateTime.now();
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
                    command = new CallbackCommand(auctionId, timestamp);
                    break;
                }
                default:
                    throw new RuntimeException("Invalid command type :" + commandType);
            }

            synchronized (json) {
                json.notifyAll();
            }

            commandBase.setCommand(command);
            auctionService.processCommand(command);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(commandBase.getRawMessage());
        }
    }
}

package command.dispatcher.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lmax.disruptor.EventHandler;
import command.commands.*;
import command.dispatcher.CommandBase;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommandParser implements EventHandler<CommandBase> {

    private final JsonParser jsonParser = new JsonParser();

    @Override
    public void onEvent(CommandBase commandBase, long l, boolean b) {
        try {
            JsonObject json = jsonParser.parse(commandBase.getRawMessage()).getAsJsonObject();
            String commandType = json.get("commandType").getAsString();
            LocalDateTime timestamp = LocalDateTime.now();
            commandBase.setTimestamp(timestamp);
            Command command;

            switch (commandType) {
                case "CancelAuction": {
                    UUID auctionId = UUID.fromString(json.get("auctionId").getAsString());
                    command = new CancelAuction(auctionId, timestamp);
                    break;
                }
                case "CreateAuction": {
                    UUID auctionId = UUID.fromString(json.get("auctionId").getAsString());
                    UUID auctioneerId = UUID.fromString(json.get("auctioneerId").getAsString());
                    UUID itemId = UUID.fromString(json.get("itemId").getAsString());
                    double startPrice = json.get("startPrice").getAsDouble();
                    command = new CreateAuction(auctionId, timestamp, auctioneerId, itemId, startPrice);
                    break;
                }
                case "EndAuction": {
                    UUID auctionId = UUID.fromString(json.get("auctionId").getAsString());
                    command = new EndAuction(auctionId, timestamp);
                    break;
                }
                case "StartAuction": {
                    UUID auctionId = UUID.fromString(json.get("auctionId").getAsString());
                    command = new StartAuction(auctionId, timestamp);
                    break;
                }
                case "PlaceBid": {
                    UUID auctionId = UUID.fromString(json.get("auctionId").getAsString());
                    UUID bidderId = UUID.fromString(json.get("bidderId").getAsString());
                    double amount = json.get("amount").getAsDouble();
                    command = new PlaceBid(auctionId, timestamp, bidderId, amount);
                    break;
                }
                default:
                    throw new RuntimeException("Invalid command type :" + commandType);
            }

            commandBase.setCommand(command);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(commandBase.getRawMessage());
        }
    }
}

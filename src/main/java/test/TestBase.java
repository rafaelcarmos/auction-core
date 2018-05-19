package test;

import com.google.gson.JsonObject;
import command.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class TestBase {

    private List<JsonObject> lst = null;

    public abstract void run(int commandsSize, Repository repository);

    public List<JsonObject> getCommands(int size) {

        List<JsonObject> lst = new ArrayList<>();

        JsonObject createAuction = new JsonObject();
        UUID auctionId = UUID.randomUUID();
        createAuction.addProperty("auctionId", auctionId.toString());
        createAuction.addProperty("commandType", "CreateAuction");
        createAuction.addProperty("auctioneerId", UUID.randomUUID().toString());
        createAuction.addProperty("itemId", UUID.randomUUID().toString());
        createAuction.addProperty("startPrice", 1200d);
        lst.add(createAuction);

        JsonObject startAuction = new JsonObject();
        startAuction.addProperty("commandType", "StartAuction");
        startAuction.addProperty("auctionId", auctionId.toString());
        lst.add(startAuction);

        for (int i = 0; i < size - 3; i++) {
            JsonObject bid = new JsonObject();
            bid.addProperty("bidderId", UUID.randomUUID().toString());
            bid.addProperty("auctionId", auctionId.toString());
            bid.addProperty("amount", 10000d + i);
            bid.addProperty("commandType", "PlaceBid");
            lst.add(bid);
        }

        JsonObject endAuction = new JsonObject();
        endAuction.addProperty("commandType", "EndAuction");
        endAuction.addProperty("auctionId", auctionId.toString());

        lst.add(endAuction);

        return lst;
    }
}

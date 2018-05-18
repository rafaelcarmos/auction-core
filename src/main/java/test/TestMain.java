package test;

import com.google.gson.JsonObject;
import command.dispatcher.ABQCommandDispatcher;
import command.dispatcher.CommandDispatcher;
import command.dispatcher.DisruptorCommandDispatcher;
import command.repository.MongoRepository;
import command.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class TestMain {
    public static void main(String[] args) {

        boolean ABQ = false;
        Repository rep = new MongoRepository("localhost:27017", "command", "events");
        CommandDispatcher dispatcher = null;

        try {

            if (ABQ) {
                dispatcher = new ABQCommandDispatcher(rep, (int) Math.pow(2, 19));
            } else {
                dispatcher = new DisruptorCommandDispatcher(rep, (int) Math.pow(2, 19));
            }

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

            for (int i = 0; i < 100000; i++) {
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

            long t1 = System.nanoTime();
            Logger.getAnonymousLogger().info("Start: " + t1);

            for (JsonObject o : lst)
                dispatcher.processCommand(o);

            dispatcher.shutdown();


            long t2 = System.nanoTime();
            Logger.getAnonymousLogger().info("End: " + t2);
            Logger.getAnonymousLogger().info("Elapsed Time: " + (t2 - t1));

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            rep.close();
        }
    }
}

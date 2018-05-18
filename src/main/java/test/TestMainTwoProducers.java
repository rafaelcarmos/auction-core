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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class TestMainTwoProducers {
    public static void main(String[] args) {

        boolean ABQ = true;
        Repository rep = new MongoRepository("localhost:27017", "command", "events");
        final CommandDispatcher dispatcher;

        try {

            dispatcher = ABQ ? new ABQCommandDispatcher(rep, (int) Math.pow(2,19)) : new DisruptorCommandDispatcher(rep, (int) Math.pow(2,19));

            List<JsonObject> lst1 = new ArrayList<>();
            List<JsonObject> lst2 = new ArrayList<>();

            JsonObject createAuction = new JsonObject();
            UUID auctionId = UUID.randomUUID();
            createAuction.addProperty("auctionId", auctionId.toString());
            createAuction.addProperty("commandType", "CreateAuction");
            createAuction.addProperty("auctioneerId", UUID.randomUUID().toString());
            createAuction.addProperty("itemId", UUID.randomUUID().toString());
            createAuction.addProperty("startPrice", 1200d);

            JsonObject startAuction = new JsonObject();
            startAuction.addProperty("commandType", "StartAuction");
            startAuction.addProperty("auctionId", auctionId.toString());

            JsonObject endAuction = new JsonObject();
            endAuction.addProperty("commandType", "EndAuction");
            endAuction.addProperty("auctionId", auctionId.toString());

            for (int i = 0; i < 50000; i++) {
                JsonObject bid = new JsonObject();
                bid.addProperty("bidderId", UUID.randomUUID().toString());
                bid.addProperty("auctionId", auctionId.toString());
                bid.addProperty("amount", 10000d + i);
                bid.addProperty("commandType", "PlaceBid");
                lst1.add(bid);
            }

            for (int i = 0; i < 50000; i++) {
                JsonObject bid = new JsonObject();
                bid.addProperty("bidderId", UUID.randomUUID().toString());
                bid.addProperty("auctionId", auctionId.toString());
                bid.addProperty("amount", 10000d + i);
                bid.addProperty("commandType", "PlaceBid");
                lst2.add(bid);
            }

            long t1 = System.nanoTime();


            Thread thread1 = Executors.defaultThreadFactory().newThread(() -> {
                try {
                    for (JsonObject o : lst1)
                        dispatcher.processCommand(o.toString());
                }catch(Exception e){
                    e.printStackTrace();
                }
            });

            Thread thread2 = Executors.defaultThreadFactory().newThread(() -> {
                try {
                    for (JsonObject o : lst2)
                        dispatcher.processCommand(o.toString());
                }catch(Exception e){
                    e.printStackTrace();
                }
            });

            dispatcher.processCommand(createAuction.toString());
            dispatcher.processCommand(startAuction.toString());
            thread1.start();
            thread2.start();
            thread1.join();
            thread2.join();
            dispatcher.processCommand(endAuction.toString());
            dispatcher.shutdown();

            long t2 = System.nanoTime();

            System.out.println("Time elapsed: " + (t2 - t1));

        } catch (Exception ex) {
            ex.printStackTrace();
        }finally{
            rep.close();
        }
    }
}

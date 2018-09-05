package test;

import com.google.gson.JsonObject;
import messaging.CommandDispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

public class TestBase {

    private int currentSize = 0;


    public TestResults run(int size, int producers, int iterations, CommandDispatcher dispatcher) throws Exception {

        UUID auctionId = UUID.randomUUID();

        List<JsonObject> commands = getCommands(size - 3, auctionId);

        String createAuction = new JsonObject();
        createAuction.addProperty("auctionId", auctionId.toString());
        createAuction.addProperty("commandType", "CreateAuction");
        createAuction.addProperty("auctioneerId", UUID.randomUUID().toString());
        createAuction.addProperty("itemId", UUID.randomUUID().toString());
        createAuction.addProperty("startPrice", 1200d);

        String startAuction = new JsonObject();
        startAuction.addProperty("commandType", "StartAuction");
        startAuction.addProperty("auctionId", auctionId.toString());

        JsonObject endAuction = new JsonObject();
        endAuction.addProperty("commandType", "EndAuction");
        endAuction.addProperty("auctionId", auctionId.toString());

        String callback = new JsonObject();
        callback.addProperty("commandType", "CallbackCommand");
        callback.addProperty("auctionId", auctionId.toString());

        dispatcher.processCommand(createAuction);
        dispatcher.processCommand(startAuction);

        Thread[] threads = new Thread[producers];
        TestResults results = new TestResults(dispatcher.getClass().getSimpleName());

        for (int i = 0; i < iterations; i++) {

            System.gc();

            for (int j = 0; j < producers; j++) {
                final int seq = j;
                threads[j] = Executors.defaultThreadFactory().newThread(() -> asyncProducer(size - 3, seq, commands, dispatcher));
            }

            long t1 = System.nanoTime();

            for (Thread t : threads) {
                t.start();
            }

            for (Thread t : threads) {
                t.join();
            }

            synchronized (callback) {
                dispatcher.processCommand(callback);
                callback.wait();
                long t2 = System.nanoTime();
                long diff = (t2 - t1);
                results.addIteration(new IterationResults(producers, diff, size));
            }
        }

        return results;
    }

    private void asyncProducer(int size, int mySeq, List<JsonObject> commands, CommandDispatcher dispatcher) {
        try {
            for (int i = mySeq; i < size; i += mySeq + 1) {
                dispatcher.processCommand(commands.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<JsonObject> getCommands(int size, UUID auctionId) {

        List<JsonObject> lst = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            JsonObject bid = new JsonObject();
            bid.addProperty("bidderId", UUID.randomUUID().toString());
            bid.addProperty("auctionId", auctionId.toString());
            bid.addProperty("amount", 10000d + i);
            bid.addProperty("commandType", "PlaceBid");
            lst.add(bid);
        }

        return lst;
    }
}

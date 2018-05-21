package test;

import com.google.gson.JsonObject;
import command.dispatcher.CommandDispatcher;
import command.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestBase {

    private int currentSize = 0;


    public void run(int size, int producers, int iterations, Repository repository, CommandDispatcher dispatcher) throws Exception {

        long[] results = new long[iterations];
        Thread[] threads = new Thread[producers];

        UUID auctionId = UUID.randomUUID();

        System.out.println("Generating commands...");
        List<JsonObject> commands = getCommands(size - 3, auctionId);
        System.out.print("OK\n");

        JsonObject createAuction = new JsonObject();
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

        JsonObject callback = new JsonObject();
        callback.addProperty("commandType", "CallbackCommand");
        callback.addProperty("auctionId", auctionId.toString());

        dispatcher.processCommand(createAuction);
        dispatcher.processCommand(startAuction);

        for (int i = 0; i < iterations; i++) {

            long t1 = System.nanoTime();

            for (int j = 0; j < producers; j++) {
                final int seq = j;
                threads[j] = Executors.defaultThreadFactory().newThread(() -> asyncProducer(size - 3, seq, commands, dispatcher));
            }

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
                results[i] = diff;
            }
        }

        long timeSum = 0;
        System.out.println(String.format("[Dispatcher: %s][Size: %d][Producers: %d]", dispatcher.getClass().getSimpleName(), size, producers));
        Columns cols = new Columns();
        cols.addLine("#", "NANO", "MICRO", "MILLI");
        for (int i = 0; i < results.length; i++) {
            timeSum += results[i];
            long micro = TimeUnit.MICROSECONDS.convert(results[i], TimeUnit.NANOSECONDS);
            long milli = TimeUnit.MILLISECONDS.convert(results[i], TimeUnit.NANOSECONDS);

            cols.addLine(String.valueOf(i), String.valueOf(results[i]), String.valueOf(micro), String.valueOf(milli));
        }

        long average = timeSum / results.length;
        long avgMicro = TimeUnit.MICROSECONDS.convert(average, TimeUnit.NANOSECONDS);
        long avgMilli = TimeUnit.MILLISECONDS.convert(average, TimeUnit.NANOSECONDS);

        long median = results.length % 2 == 0 ? (results[results.length / 2] + results[(results.length / 2) + 1]) / 2 : results[(results.length / 2) + 1];
        long mdnMicro = TimeUnit.MICROSECONDS.convert(median, TimeUnit.NANOSECONDS);
        long mdnMilli = TimeUnit.MILLISECONDS.convert(median, TimeUnit.NANOSECONDS);

        cols.addLine("AVG", String.valueOf(average), String.valueOf(avgMicro), String.valueOf(avgMilli));
        cols.addLine("MDN", String.valueOf(median), String.valueOf(mdnMicro), String.valueOf(mdnMilli));
        cols.print();
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

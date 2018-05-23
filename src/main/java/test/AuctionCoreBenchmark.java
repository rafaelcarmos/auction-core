package test;

import com.google.gson.JsonObject;
import command.dispatcher.CommandDispatcher;
import command.dispatcher.DisruptorCommandDispatcher;
import command.repository.MongoRepository;
import command.repository.Repository;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.UUID;

public class AuctionCoreBenchmark {

    CommandDispatcher disruptorCommandDispatcher = null;
    private UUID auctionId = UUID.randomUUID();

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(AuctionCoreBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() throws Exception {

        Repository rep = new MongoRepository("localhost:27017", "command", "events");
        int bufferSize = (int) Math.pow(2, 13);

        disruptorCommandDispatcher = new DisruptorCommandDispatcher(rep, bufferSize);

        JsonObject createAuction = new JsonObject();
        createAuction.addProperty("auctionId", auctionId.toString());
        createAuction.addProperty("commandType", "CreateAuction");
        createAuction.addProperty("auctioneerId", UUID.randomUUID().toString());
        createAuction.addProperty("itemId", UUID.randomUUID().toString());

        JsonObject startAuction = new JsonObject();
        startAuction.addProperty("commandType", "StartAuction");
        startAuction.addProperty("auctionId", auctionId.toString());

        disruptorCommandDispatcher.processCommand(createAuction);
        disruptorCommandDispatcher.processCommand(startAuction);
    }

    @Benchmark
    public void disruptorBenchmark() throws Exception {
        JsonObject bid = new JsonObject();
        bid.addProperty("bidderId", UUID.randomUUID().toString());
        bid.addProperty("auctionId", auctionId.toString());
        bid.addProperty("amount", 10000d);
        bid.addProperty("commandType", "PlaceBid");
        disruptorCommandDispatcher.processCommand(bid);
        synchronized (bid) {
            bid.wait();
        }
    }
}

package test;

import domain.auction.commands.CommandType;
import domain.auction.repository.InMemoryRepository;
import domain.auction.repository.Repository;
import domain.auction.service.AuctionService;
import domain.auction.service.AuctionServiceImpl;
import messaging.CommandDispatcher;
import messaging.CommandDispatcherFactory;
import messaging.dispatchers.DisruptorCommandDispatcher;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SingleCommandLatencyTest {

    public static void main(String args[]) {

        int size = 1000000;
        int bufferSize = 1 * (1024 * 1024);
        CountDownLatch latch = new CountDownLatch(1);

        CommandDispatcher dispatcher = null;
        Repository repository = new InMemoryRepository();
        AuctionService auctionService = new AuctionServiceImpl(repository);

        try {

            if (args.length > 0)
                size = Integer.parseInt(args[0]);

            if (args.length > 1)
                dispatcher = CommandDispatcherFactory.getDispatcher(args[1], auctionService, bufferSize, latch);
            else
                dispatcher = new DisruptorCommandDispatcher(auctionService, bufferSize, latch);


            UUID auctionId = UUID.randomUUID();

            String createAuction = CommandType.CREATE_AUCTION + ";" + auctionId + ";-1;-1";
            dispatcher.processCommand(createAuction);

            String startAuction = CommandType.START_AUCTION + ";" + auctionId;
            dispatcher.processCommand(startAuction);

            String placeBid = CommandType.PLACE_BID + ";" + auctionId + ";-1;10.00";

            Columns cols = new Columns();
            cols.addLine("#", "Size", "Type", "Nanoseconds", "Medium Latency (nano)");

            long[] latencies = new long[size];

            System.gc();

            for (int commandCount = 0; commandCount < size; commandCount++) {

                long timerStart = System.nanoTime();

                dispatcher.processCommand(placeBid);

                latch.await();

                long timerEnd = System.nanoTime();
                long elapsedTimeNano = timerEnd - timerStart;
                latencies[commandCount] = elapsedTimeNano;

                latch = new CountDownLatch(1);
                dispatcher.setLatch(latch);
            }

            Arrays.sort(latencies);

            long threshold = 10;

            long belowOneHundredMicro = 0;
            for (long latency : latencies)
                if (TimeUnit.NANOSECONDS.toMicros(latency) <= threshold)
                    belowOneHundredMicro++;


            System.out.println("LATENCIES:");
            System.out.format("Below %d microseconds: %d (%f%%)\n", threshold, belowOneHundredMicro, (double) belowOneHundredMicro * 100 / size);
            System.out.format("MIN: %d\n", TimeUnit.NANOSECONDS.toMicros(latencies[0]));
            System.out.format("MDN: %d\n", TimeUnit.NANOSECONDS.toMicros(latencies[size / 2]));
            System.out.format("MAX: %d\n", TimeUnit.NANOSECONDS.toMicros(latencies[size - 1]));

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            Objects.requireNonNull(dispatcher).shutdown();
        }
    }
}

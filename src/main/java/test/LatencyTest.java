package test;

import domain.auction.commands.CommandType;
import domain.auction.repository.InMemoryRepository;
import domain.auction.repository.Repository;
import domain.auction.service.AuctionService;
import domain.auction.service.AuctionServiceImpl;
import messaging.CommandDispatcher;
import messaging.CommandDispatcherFactory;
import messaging.dispatchers.DisruptorCommandDispatcher;

import java.text.NumberFormat;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class LatencyTest {

    public static void main(String args[]) {

        int size = 1 * (1000 * 1000);
        int iterations = 10;
        int bufferSize = 1 * (1024 * 1024);
        CountDownLatch latch = new CountDownLatch(size);

        CommandDispatcher dispatcher = null;
        Repository repository = new InMemoryRepository();
        AuctionService auctionService = new AuctionServiceImpl(repository);

        try {

            if (args.length > 0)
                size = Integer.parseInt(args[0]);

            if (args.length > 1)
                iterations = Integer.parseInt(args[1]);

            if (args.length > 2)
                dispatcher = CommandDispatcherFactory.getDispatcher(args[2], auctionService, bufferSize, latch);
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

            for (int iteration = 0; iteration < iterations; iteration++) {

                long timerStart = System.nanoTime();

                for (int commandCount = 0; commandCount < size; commandCount++)
                    dispatcher.processCommand(placeBid);

                latch.await();

                long timerEnd = System.nanoTime();
                long elapsedTimeNano = timerEnd - timerStart;
                double latency = elapsedTimeNano / size;

                cols.addLine(Integer.toString(iteration), Long.toString(size), dispatcher.getClass().getSimpleName(), Long.toString(elapsedTimeNano), NumberFormat.getIntegerInstance().format(latency));

                cols.print();
                latch = new CountDownLatch(size);
                dispatcher.setLatch(latch);

                System.gc();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            Objects.requireNonNull(dispatcher).shutdown();
        }
    }
}

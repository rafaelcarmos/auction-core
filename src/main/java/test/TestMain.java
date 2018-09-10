package test;

import domain.auction.commands.CommandType;
import domain.auction.repository.InMemoryRepository;
import domain.auction.repository.Repository;
import domain.auction.service.AuctionService;
import domain.auction.service.AuctionServiceImpl;
import messaging.CommandDispatcher;
import messaging.CommandDispatcherFactory;
import messaging.dispatchers.DisruptorCommandDispatcher;

import java.util.UUID;

public class TestMain {

    public static void main(String args[]) {

        int size = 1000000;
        int iterations = 5;
        int bufferSize = (int) Math.pow(2d, 12d);

        CommandDispatcher dispatcher = null;
        Repository repository = new InMemoryRepository();
        AuctionService auctionService = new AuctionServiceImpl(repository);

        try {

            if (args.length > 0)
                size = Integer.parseInt(args[0]);

            if (args.length > 1)
                iterations = Integer.parseInt(args[1]);

            if (args.length > 2)
                dispatcher = CommandDispatcherFactory.getDispatcher(args[2], auctionService, bufferSize);
            else
                dispatcher = new DisruptorCommandDispatcher(auctionService, bufferSize);


            UUID auctionId = UUID.randomUUID();

            String createAuction = CommandType.CREATE_AUCTION + ";" + auctionId + ";-1;-1";
            dispatcher.processCommand(createAuction);

            String startAuction = CommandType.START_AUCTION + ";" + auctionId;
            dispatcher.processCommand(startAuction);

            String placeBid = CommandType.PLACE_BID + ";" + auctionId + ";-1;10.00";

            Columns cols = new Columns();
            cols.addLine("#", "Type", "Nano", "Throughput Msgs/second");

            for (int iteration = 0; iteration < iterations; iteration++) {

                long startNano = System.nanoTime();

                for (int commandCount = 0; commandCount < size; commandCount++)
                    dispatcher.processCommand(placeBid);

                long endNano = System.nanoTime();
                long elapsedNano = endNano - startNano;
                double elapsedSeconds = (double) elapsedNano / 1000000000;
                double throughput = size / elapsedSeconds;

                cols.addLine(Integer.toString(iteration), dispatcher.getClass().getSimpleName(), Long.toString(elapsedNano), Double.toString(throughput));
                cols.print();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dispatcher.shutdown();
        }
    }
}

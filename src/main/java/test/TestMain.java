package test;

import domain.auction.commands.CommandType;
import domain.auction.repository.InMemoryRepository;
import domain.auction.repository.Repository;
import domain.auction.service.AuctionService;
import domain.auction.service.AuctionServiceImpl;
import messaging.CommandDispatcher;
import messaging.CommandDispatcherFactory;
import messaging.dispatchers.ABQCommandDispatcher;

import java.text.NumberFormat;
import java.util.UUID;

public class TestMain {

    public static void main(String args[]) {

        int size = 100000;
        int iterations = 1;
        int bufferSize = (1024 * 1024);

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
                dispatcher = new ABQCommandDispatcher(auctionService, bufferSize);


            UUID auctionId = UUID.randomUUID();

            String createAuction = CommandType.CREATE_AUCTION + ";" + auctionId + ";-1;-1";
            dispatcher.processCommand(createAuction);

            String startAuction = CommandType.START_AUCTION + ";" + auctionId;
            dispatcher.processCommand(startAuction);

            String placeBid = CommandType.PLACE_BID + ";" + auctionId + ";-1;10.00";

            Columns cols = new Columns();
            cols.addLine("#", "Type", "Nano", "Messages/ms");

            for (int iteration = 0; iteration < iterations; iteration++) {

                long startNano = System.nanoTime();

                for (int commandCount = 0; commandCount < size; commandCount++)
                    dispatcher.processCommand(placeBid);

//                String callbackCommand = "CALLBACK_COMMAND;" + auctionId;
//
//                dispatcher.processCommand(callbackCommand);
//
//                synchronized (callbackCommand) {
//                    callbackCommand.wait();
//                }

                long endNano = System.nanoTime();
                long elapsedNano = endNano - startNano;
                double elapsedMiliseconds = elapsedNano / 1000000;
                double throughput = size / elapsedMiliseconds;
                cols.addLine(Integer.toString(iteration), dispatcher.getClass().getSimpleName(), Long.toString(elapsedNano), NumberFormat.getIntegerInstance().format(throughput));
                cols.print();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dispatcher.shutdown();
        }
    }
}

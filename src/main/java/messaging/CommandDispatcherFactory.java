package messaging;

import domain.auction.service.AuctionService;
import messaging.dispatchers.ABQCommandDispatcher;
import messaging.dispatchers.DisruptorCommandDispatcher;

import java.util.concurrent.CountDownLatch;

public class CommandDispatcherFactory {

    public static CommandDispatcher getDispatcher(String name, AuctionService auctionService, int size, CountDownLatch latch)
            throws Exception {

        CommandDispatcher dispatcher;

        switch (name.toUpperCase()) {
            case "ABQ":
                dispatcher = new ABQCommandDispatcher(auctionService, size, latch);
                break;
            case "DISRUPTOR":
                dispatcher = new DisruptorCommandDispatcher(auctionService, size, latch);
                break;
            default:
                throw new IllegalArgumentException("Dispatcher type not found: " + name);
        }

        return dispatcher;
    }
}

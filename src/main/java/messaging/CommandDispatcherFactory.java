package messaging;

import domain.auction.service.AuctionService;
import messaging.dispatchers.ArrayBlockingQueueDispatcher;
import messaging.dispatchers.DisruptorDispatcher;

import java.util.concurrent.CountDownLatch;

public class CommandDispatcherFactory {

    public static CommandDispatcher getDispatcher(String name, AuctionService auctionService, int size, CountDownLatch latch)
            throws Exception {

        CommandDispatcher dispatcher;

        switch (name.toUpperCase()) {
            case "ABQ":
                dispatcher = new ArrayBlockingQueueDispatcher(auctionService, size, latch);
                break;
            case "DISRUPTOR":
                dispatcher = new DisruptorDispatcher(auctionService, size, latch);
                break;
            default:
                throw new IllegalArgumentException("Dispatcher type not found: " + name);
        }

        return dispatcher;
    }
}

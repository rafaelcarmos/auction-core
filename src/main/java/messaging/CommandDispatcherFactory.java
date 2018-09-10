package messaging;

import domain.auction.service.AuctionService;
import messaging.dispatchers.ABQCommandDispatcher;
import messaging.dispatchers.DisruptorCommandDispatcher;

public class CommandDispatcherFactory {

    public static CommandDispatcher getDispatcher(String name, AuctionService auctionService, int bufferSize)
            throws Exception {

        CommandDispatcher dispatcher = null;

        switch (name.toUpperCase()) {
            case "ABQ":
                dispatcher = new ABQCommandDispatcher(auctionService, bufferSize);
                break;
            case "DISRUPTOR":
                dispatcher = new DisruptorCommandDispatcher(auctionService, bufferSize);
                break;
            default:
                throw new IllegalArgumentException("Dispatcher type not found: " + name);
        }

        return dispatcher;
    }
}

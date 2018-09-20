package test;

import messaging.dispatchers.CommandDispatcher;

import java.util.UUID;

public class BenchmarkUtils {

    public static String PrepareDispatcherAndGetPlaceBidCommand(CommandDispatcher dispatcher) throws Exception {

        UUID auctionId = UUID.randomUUID();

        String createAuction = "CREATE_AUCTION;" + auctionId.toString() + ";-1;-1";
        String startAuction = "START_AUCTION;" + auctionId.toString();
        String placeBid = "PLACE_BID;" + auctionId.toString() + ";-1;1000";

        dispatcher.processCommand(createAuction);
        dispatcher.processCommand(startAuction);

        return placeBid;
    }

}

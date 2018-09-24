package test.util;

import messaging.dispatchers.CommandDispatcher;

import java.util.UUID;

public class BenchmarkUtils {

    public static byte[] PrepareDispatcherAndGetPlaceBidCommand(CommandDispatcher dispatcher) throws Exception {

        UUID auctionId = UUID.randomUUID();

        byte[] createAuction = ("CREATE_AUCTION;" + auctionId.toString() + ";-1;-1").getBytes();
        byte[] startAuction = ("START_AUCTION;" + auctionId.toString()).getBytes();
        byte[] placeBid = ("PLACE_BID;" + auctionId.toString() + ";-1;1000").getBytes();

        dispatcher.processCommand(createAuction);
        dispatcher.processCommand(startAuction);

        return placeBid;
    }

}

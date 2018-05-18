package query;

import command.events.Event;
import query.model.Auction;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QueryEventBusImpl implements QueryEventBus {

    private Map<UUID, Auction> auctionMap;

    public QueryEventBusImpl() {

    }

    @Override
    public void broadcast(Event e) {
        auctionMap = new HashMap<>();
    }
}

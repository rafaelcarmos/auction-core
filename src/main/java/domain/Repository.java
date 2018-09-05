package domain;

import domain.auction.Auction;
import domain.auction.events.Event;

import java.util.List;

public interface Repository {

    void save(Event e) throws Exception;

    void save(Iterable<Event> e) throws Exception;

    List<Event> getEvents();

    Auction getAuction(long id);

    Auction createAndGetAuction(long id);

    void close();
}

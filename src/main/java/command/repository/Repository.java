package command.repository;

import command.aggregate.Auction;
import command.events.Event;
import command.repository.exceptions.AggregateExistentException;
import command.repository.exceptions.AggregateNonExistentException;

import java.util.List;
import java.util.UUID;

public interface Repository {

    void save(Event e);

    void save(Iterable<Event> e);

    List<Event> getEvents();

    Auction getAuction(UUID id) throws AggregateNonExistentException;

    Auction createAndGetAuction(UUID id) throws AggregateExistentException;

    void close();
}

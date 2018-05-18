package command.repository;

import command.aggregate.Auction;
import command.events.Event;
import command.repository.exceptions.AggregateExistantException;
import command.repository.exceptions.AggregateNonExistantException;

import java.util.List;
import java.util.UUID;

public interface Repository {

    void save(Event e);

    void save(Iterable<Event> e);

    List<Event> getEvents();

    Auction getAuction(UUID id) throws AggregateNonExistantException;

    Auction createAndGetAuction(UUID id) throws AggregateExistantException;

    void close();
}

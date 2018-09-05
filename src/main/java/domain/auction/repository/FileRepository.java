package domain.auction.repository;

import auction.events.*;
import domain.Repository;
import domain.auction.Auction;
import domain.auction.events.Event;
import domain.auction.repository.exceptions.AggregateExistentException;
import domain.auction.repository.exceptions.AggregateNonExistentException;
import org.bson.Document;

import java.io.RandomAccessFile;
import java.util.List;

public class FileRepository implements Repository {

    RandomAccessFile raf;

    public FileRepository() throws Exception {
        raf = new RandomAccessFile("randomAccessFileTest", "rw");
    }

    @Override
    public void save(Event e) {

    }

    @Override
    public void save(Iterable<Event> events) {

    }

    @Override
    public List<Event> getEvents() {

    }

    private Event parseEvent(Document d) {

    }

    @Override
    public Auction getAuction(long id) throws AggregateNonExistentException {

    }

    @Override
    public Auction createAndGetAuction(long id) throws AggregateExistentException {

    }

    @Override
    public void close() {

    }
}

package domain.auction.repository;

import domain.auction.Auction;

import java.util.HashMap;


public class InMemoryRepository implements Repository {

    HashMap<Long, Auction> auctionsById;

    public InMemoryRepository() {
        auctionsById = new HashMap<>();
    }

    @Override
    public Auction getAuction(long id) {
        return auctionsById.get(id);
    }

    @Override
    public Auction createAndGetAuction(long id) {
        Auction auction = new Auction(id);
        auctionsById.put(id, auction);
        return auction;
    }

    @Override
    public void close() {

    }
}

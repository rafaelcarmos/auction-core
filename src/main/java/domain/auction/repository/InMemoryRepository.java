package domain.auction.repository;

import domain.auction.Auction;

import java.util.HashMap;


public class InMemoryRepository implements Repository {

    final HashMap<String, Auction> auctionsById;

    public InMemoryRepository() {
        auctionsById = new HashMap<>();
    }

    @Override
    public Auction getAuction(String id) {
        return auctionsById.get(id);
    }

    @Override
    public Auction createAndGetAuction(String id) {
        Auction auction = new Auction(id);
        auctionsById.put(id, auction);
        return auction;
    }

    @Override
    public void close() {

    }
}

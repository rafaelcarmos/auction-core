package domain.auction.repository;

import domain.auction.Auction;

import java.util.HashMap;
import java.util.UUID;


public class InMemoryRepository implements Repository {

    HashMap<UUID, Auction> auctionsById;

    public InMemoryRepository() {
        auctionsById = new HashMap<>();
    }

    @Override
    public Auction getAuction(UUID id) {
        return auctionsById.get(id);
    }

    @Override
    public Auction createAndGetAuction(UUID id) {
        Auction auction = new Auction(id);
        auctionsById.put(id, auction);
        return auction;
    }

    @Override
    public void close() {

    }
}

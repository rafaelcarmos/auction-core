package domain.auction.repository;

import domain.auction.Auction;

import java.util.UUID;

public interface Repository {

    Auction getAuction(UUID id);

    Auction createAndGetAuction(UUID id);

    void close();
}

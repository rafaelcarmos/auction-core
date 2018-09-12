package domain.auction.repository;

import domain.auction.Auction;

public interface Repository {

    Auction getAuction(String id);

    Auction createAndGetAuction(String id);

    void close();
}

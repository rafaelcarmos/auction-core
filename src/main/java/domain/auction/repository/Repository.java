package domain.auction.repository;

import domain.auction.Auction;

public interface Repository {

    Auction getAuction(long id);

    Auction createAndGetAuction(long id);

    void close();
}

package query.model;

import command.aggregate.exceptions.AuctionCancelledException;
import command.aggregate.exceptions.AuctionEndedException;
import command.aggregate.exceptions.AuctionNotStartedException;
import command.aggregate.exceptions.InvalidBidException;
import command.commands.*;
import command.events.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Auction {

    private final UUID id;
    private UUID auctioneerId;
    private UUID itemId;
    private double startPrice;
    private UUID currentWinnerId;
    private double currentWinningBid = 0;
    private AuctionState state;
    private List<Bid> bids;

    public Auction(UUID id) {
        this.id = id;
    }



    public enum AuctionState {
        CREATED,
        STARTED,
        CANCELLED,
        ENDED
    }
}

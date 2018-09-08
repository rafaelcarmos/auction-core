package domain.auction;

import domain.auction.commands.*;
import domain.auction.events.*;
import domain.auction.exceptions.AuctionCancelledException;
import domain.auction.exceptions.AuctionEndedException;
import domain.auction.exceptions.AuctionNotStartedException;

import java.util.ArrayList;
import java.util.List;

public class Auction {
    private final long id;
    private long auctioneerId;
    private long itemId;
    private long currentWinnerId;
    private double currentWinningBid = 0;
    private AuctionState state;

    public Auction(long id) {
        this.id = id;
    }

    public Event handle(Command command) throws Exception {
        return command.accept(this);
    }

    public void apply(Event event) {

    }

    public long getId() {
        return id;
    }

    public AuctionFinished onCommand(FinishAuction cmd) throws Exception {

        if (state == AuctionState.CREATED) {
            throw new AuctionNotStartedException("Auction hasn't started");
        }

        if (state == AuctionState.CANCELLED) {
            throw new AuctionCancelledException("Auction has already been cancelled");
        }

        if (state == AuctionState.ENDED) {
            throw new AuctionEndedException("Auction has already ended");
        }

        return new AuctionFinished(cmd.getAuctionId(), cmd.getTimestamp());
    }

    public AuctionCancelled onCommand(CancelAuction cmd) throws Exception {

        if (state == AuctionState.CREATED) {
            throw new AuctionNotStartedException("Auction hasn't started");
        }

        if (state == AuctionState.CANCELLED) {
            throw new AuctionCancelledException("Auction has already been cancelled");
        }

        if (state == AuctionState.ENDED) {
            throw new AuctionEndedException("Auction has already ended");
        }

        return new AuctionCancelled(cmd.getAuctionId(), cmd.getTimestamp());
    }

    public AuctionCreated onCommand(CreateAuction cmd) {

        return new AuctionCreated(cmd.getAuctionId(), cmd.getTimestamp(), cmd.getAuctioneerId(), cmd.getItemId());
    }

    public void onEvent(AuctionFinished evt) {
        state = AuctionState.ENDED;
    }

    public BidPlaced onCommand(PlaceBid cmd) throws Exception {

        if (state == AuctionState.CREATED) {
            throw new AuctionNotStartedException("Auction hasn't started");
        }

        if (state == AuctionState.CANCELLED) {
            throw new AuctionCancelledException("Auction has already been cancelled");
        }

        if (state == AuctionState.ENDED) {
            throw new AuctionEndedException("Auction has already ended");
        }

        return new BidPlaced(cmd.getAuctionId(), cmd.getTimestamp(), cmd.getBidderId(), cmd.getAmount());
    }

    public AuctionStarted onCommand(StartAuction cmd) throws Exception {

        List<Event> result = new ArrayList<>();

        if (state == AuctionState.STARTED) {
            throw new AuctionNotStartedException("Auction has already started");
        }

        if (state == AuctionState.CANCELLED) {
            throw new AuctionCancelledException("Auction has already been cancelled");
        }

        if (state == AuctionState.ENDED) {
            throw new AuctionEndedException("Auction has already ended");
        }

        return new AuctionStarted(cmd.getAuctionId(), cmd.getTimestamp());
    }

    public void onEvent(AuctionCancelled evt) {
        state = AuctionState.CANCELLED;
    }

    public void onEvent(AuctionCreated evt) {
        auctioneerId = evt.getAuctioneerId();
        itemId = evt.getItemId();
        state = AuctionState.CREATED;
    }

    public enum AuctionState {
        CREATED,
        STARTED,
        CANCELLED,
        ENDED
    }

    public void onEvent(BidPlaced evt) {
        if (evt.getAmount() > currentWinningBid) {
            currentWinnerId = evt.getBidderId();
            currentWinningBid = evt.getAmount();
        }
    }

    public void onEvent(AuctionStarted evt) {
        state = AuctionState.STARTED;
    }

}

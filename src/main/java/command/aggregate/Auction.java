package command.aggregate;

import command.aggregate.exceptions.AuctionCancelledException;
import command.aggregate.exceptions.AuctionEndedException;
import command.aggregate.exceptions.AuctionNotStartedException;
import command.aggregate.exceptions.InvalidBidException;
import command.commands.*;
import command.events.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Auction {
    private final UUID id;
    private static final List<Method> handleMethods = Arrays.stream(Auction.class.getMethods()).filter(m -> m.getName().contains("onCommand")).collect(Collectors.toList());
    private static final List<Method> applyMethods = Arrays.stream(Auction.class.getMethods()).filter(m -> m.getName().contains("onEvent")).collect(Collectors.toList());
    private UUID auctioneerId;
    private UUID itemId;
    private double startPrice;
    private UUID currentWinnerId;
    private double currentWinningBid = 0;
    private AuctionState state;

    public Auction(UUID id) {
        this.id = id;
    }

    public Event handle(Command c) throws Exception {
        for (Method m : handleMethods)
            if (m.getParameterTypes()[0].equals(c.getClass()))
                return (Event) m.invoke(this, c);

        return null;
    }

    public void apply(Event e) throws Exception {
        for (Method m : applyMethods)
            if (m.getParameterTypes()[0].equals(e.getClass()))
                m.invoke(this, e);
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

        List<Event> result = new ArrayList<Event>();

        return new AuctionCreated(cmd.getAuctionId(), cmd.getTimestamp(), cmd.getAuctioneerId(), cmd.getItemId(), cmd.getStartPrice());
    }

    public AuctionEnded onCommand(EndAuction cmd) throws Exception {

        List<Event> result = new ArrayList<Event>();

        if (state == AuctionState.CREATED) {
            throw new AuctionNotStartedException("Auction hasn't started");
        }

        if (state == AuctionState.CANCELLED) {
            throw new AuctionCancelledException("Auction has already been cancelled");
        }

        if (state == AuctionState.ENDED) {
            throw new AuctionEndedException("Auction has already ended");
        }

        return new AuctionEnded(cmd.getAuctionId(), cmd.getTimestamp());
    }

    public BidPlaced onCommand(PlaceBid cmd) throws Exception {

        List<Event> result = new ArrayList<Event>();

        if (state == AuctionState.CREATED) {
            throw new AuctionNotStartedException("Auction hasn't started");
        }

        if (state == AuctionState.CANCELLED) {
            throw new AuctionCancelledException("Auction has already been cancelled");
        }

        if (state == AuctionState.ENDED) {
            throw new AuctionEndedException("Auction has already ended");
        }

        if (currentWinningBid == 0)
            if (cmd.getAmount() < startPrice)
                throw new InvalidBidException("Bid amount is less than start price");

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
        startPrice = evt.getStartPrice();
        state = AuctionState.CREATED;
    }

    public void onEvent(AuctionEnded evt) {
        state = AuctionState.ENDED;
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

    public enum AuctionState {
        CREATED,
        STARTED,
        CANCELLED,
        ENDED
    }
}

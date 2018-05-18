package command.service;

import command.aggregate.Auction;
import command.aggregate.exceptions.InvalidCommandException;
import command.commands.Command;
import command.commands.CreateAuction;
import command.events.AuctionCreated;
import command.events.Event;
import command.repository.Repository;

public class AuctionServiceImpl implements AuctionService {

    private final Repository repository;

    public AuctionServiceImpl(Repository repository) {
        this.repository = repository;
        //this.replayAllEvents();
    }

    @Override
    public void processCommand(Command command) throws Exception {

        //Get aggregate from repository
        Auction auction;
        if (command instanceof CreateAuction)
            auction = repository.createAndGetAuction(command.getAuctionId());
        else
            auction = repository.getAuction(command.getAuctionId());

        //Handle command then apply event
        Event event = auction.handle(command);
        if (event != null) {
            auction.apply(event);
            //repository.save(event);
        } else
            throw new InvalidCommandException("Could not handle " + command.getClass().getName());

    }

    @Override
    public void replayAllEvents() throws Exception {

        Iterable<Event> events = repository.getEvents();

        for (Event e : events) {

            Auction auction;

            if (e instanceof AuctionCreated) {
                auction = repository.createAndGetAuction(e.getAuctionId());
            } else {
                auction = repository.getAuction(e.getAuctionId());
            }

            auction.apply(e);
        }
    }
}

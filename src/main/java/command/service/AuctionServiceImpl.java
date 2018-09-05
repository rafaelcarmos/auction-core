package command.service;

import domain.Repository;
import domain.auction.Auction;
import domain.auction.commands.Command;
import domain.auction.commands.CreateAuction;
import domain.auction.events.AuctionCreated;
import domain.auction.events.Event;

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
        }
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

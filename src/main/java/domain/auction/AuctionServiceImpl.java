package domain.auction;

import domain.auction.commands.Command;
import domain.auction.commands.CreateAuction;
import domain.auction.events.Event;
import domain.auction.repository.Repository;

public class AuctionServiceImpl implements AuctionService {

    private final Repository repository;

    public AuctionServiceImpl(Repository repository) {
        this.repository = repository;
        //this.replayHistory();
    }

    @Override
    public void processCommand(Command command) throws Exception {

        Auction auction;

        if (command instanceof CreateAuction)
            auction = repository.createAndGetAuction(command.getAuctionId());
        else
            auction = repository.getAuction(command.getAuctionId());

        Event event = auction.handle(command);
        if (event != null) {
            auction.apply(event);
        }
    }

    @Override
    public void replayHistory() {

    }

    @Override
    public void close() {
        repository.close();
    }
}

package domain.auction;

import domain.auction.commands.Command;

public interface AuctionService {

    void processCommand(Command command) throws Exception;

    void replayHistory() throws Exception;

    void close();

}

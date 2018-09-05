package command.service;

import domain.auction.commands.Command;

public interface AuctionService {
    void processCommand(Command command) throws Exception;

    void replayAllEvents() throws Exception;
}

package command.service;

import command.commands.Command;

public interface AuctionService {
    void processCommand(Command command) throws Exception;

    void replayAllEvents() throws Exception;
}

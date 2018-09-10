package messaging.dispatchers;

import domain.auction.service.AuctionService;
import messaging.CommandBase;
import messaging.CommandDispatcher;
import messaging.handlers.CommandJournaler;
import messaging.handlers.CommandParser;
import messaging.handlers.CommandProcessor;

public class SingleThreadedCommandDispatcher implements CommandDispatcher {


    private long sequence = 0;
    private AuctionService auctionService;
    private CommandJournaler journaler;
    private CommandParser parser;
    private CommandProcessor processor;

    public SingleThreadedCommandDispatcher(AuctionService auctionService, int queueSize) throws Exception {

        this.auctionService = auctionService;
        this.journaler = new CommandJournaler();
        this.parser = new CommandParser();
        this.processor = new CommandProcessor(auctionService);
    }

    @Override
    public void processCommand(String rawMessage) {

        CommandBase command = new CommandBase();
        command.setRawMessage(rawMessage);

        journaler.onEvent(command, sequence, false);
        parser.onEvent(command, sequence, false);
        processor.onEvent(command, sequence, false);
        sequence++;

    }

    @Override
    public void shutdown() {
    }
}

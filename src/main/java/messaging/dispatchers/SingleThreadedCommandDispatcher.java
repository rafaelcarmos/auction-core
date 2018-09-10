package messaging.dispatchers;

import domain.auction.repository.Repository;
import domain.auction.service.AuctionServiceImpl;
import messaging.CommandBase;
import messaging.CommandDispatcher;
import messaging.handlers.CommandJournaler;
import messaging.handlers.CommandParser;
import messaging.handlers.CommandProcessor;

public class SingleThreadedCommandDispatcher implements CommandDispatcher {


    private long sequence = 0;
    private CommandJournaler journaler;
    private CommandParser parser;
    private CommandProcessor processor;

    public SingleThreadedCommandDispatcher(Repository repository, int queueSize) throws Exception {

        this.journaler = new CommandJournaler();
        this.parser = new CommandParser();
        this.processor = new CommandProcessor(new AuctionServiceImpl(repository));
    }

    @Override
    public void processCommand(String rawMessage) throws Exception {
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

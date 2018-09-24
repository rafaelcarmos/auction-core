package messaging.dispatchers;

import domain.auction.service.AuctionService;
import messaging.handlers.CommandJournaler;
import messaging.handlers.CommandParser;
import messaging.handlers.CommandProcessor;

import java.util.concurrent.CountDownLatch;

public abstract class CommandDispatcher {

    protected final int size;
    protected final CommandJournaler journaler;
    protected final CommandParser parser;
    protected final CommandProcessor processor;
    protected final AuctionService auctionService;
    protected CountDownLatch latch;

    public CommandDispatcher(AuctionService auctionService, int size, CountDownLatch latch) throws Exception {

        this.size = size;
        this.auctionService = auctionService;
        this.journaler = new CommandJournaler();
        this.parser = new CommandParser();
        this.processor = new CommandProcessor(auctionService, latch);
        this.latch = latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
        this.processor.setLatch(latch);
    }

    public abstract void processCommand(byte[] rawMessage) throws Exception;

    public abstract void shutdown();
}

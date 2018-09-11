package messaging.dispatchers;

import domain.auction.service.AuctionService;
import messaging.CommandBase;
import messaging.CommandDispatcher;
import messaging.handlers.SingleThreadedCommandHandler;

public class SingleThreadedCommandDispatcher implements CommandDispatcher {


    private long sequence = 0;
    private AuctionService auctionService;
    private SingleThreadedCommandHandler handler;

    public SingleThreadedCommandDispatcher(AuctionService auctionService, int queueSize) throws Exception {

        this.auctionService = auctionService;
        handler = new SingleThreadedCommandHandler(auctionService);
    }

    @Override
    public void processCommand(String rawMessage) {

        CommandBase command = new CommandBase();
        command.setRawMessage(rawMessage);
        handler.onEvent(command, sequence, true);
        sequence++;
    }

    @Override
    public void shutdown() {
    }
}

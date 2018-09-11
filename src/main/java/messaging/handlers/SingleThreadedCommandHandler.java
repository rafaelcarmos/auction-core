package messaging.handlers;

import com.lmax.disruptor.EventHandler;
import domain.auction.service.AuctionService;
import messaging.CommandBase;

public class SingleThreadedCommandHandler implements EventHandler<CommandBase> {

    private AuctionService auctionService;
    private CommandJournaler journaler;
    private CommandParser parser;
    private CommandProcessor processor;

    public SingleThreadedCommandHandler(AuctionService auctionService) throws Exception {

        journaler = new CommandJournaler();
        parser = new CommandParser();
        processor = new CommandProcessor(auctionService);

    }

    @Override
    public void onEvent(CommandBase commandBase, long sequence, boolean endOfBatch) {
        try {

            journaler.onEvent(commandBase, sequence, false);
            parser.onEvent(commandBase, sequence, false);
            processor.onEvent(commandBase, sequence, false);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(commandBase.getRawMessage());
        }
    }
}

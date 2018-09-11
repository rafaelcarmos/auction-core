package messaging.handlers;

import com.lmax.disruptor.EventHandler;
import domain.auction.service.AuctionService;
import messaging.CommandBase;

public class SingleThreadedCommandHandler implements EventHandler<CommandBase> {

    private final AuctionService auctionService;
    private final CommandJournaler journaler;
    private final CommandParser parser;
    private final CommandProcessor processor;

    public SingleThreadedCommandHandler(AuctionService auctionService) throws Exception {

        this.auctionService = auctionService;
        journaler = new CommandJournaler();
        parser = new CommandParser();
        processor = new CommandProcessor(auctionService);
    }

    @Override
    public void onEvent(CommandBase commandBase, long sequence, boolean endOfBatch) {

        try {

            journaler.onEvent(commandBase, sequence, endOfBatch);
            parser.onEvent(commandBase, sequence, endOfBatch);
            processor.onEvent(commandBase, sequence, endOfBatch);

        } catch (Exception ex) {

            ex.printStackTrace();
            System.out.println(commandBase.getRawMessage());
        }
    }

    public void close() {

        try {

            journaler.close();
            auctionService.close();

        } catch (Exception ex) {

        }
    }
}

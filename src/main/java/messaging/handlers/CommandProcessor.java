package messaging.handlers;

import com.lmax.disruptor.EventHandler;
import domain.auction.service.AuctionService;
import messaging.CommandBase;

public class CommandProcessor implements EventHandler<CommandBase> {

    private final AuctionService auctionService;

    public CommandProcessor(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @Override
    public void onEvent(CommandBase commandBase, long sequence, boolean endOfBatch) {
        try {

            auctionService.processCommand(commandBase.getCommand());

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(commandBase.getRawMessage());
        }
    }
}

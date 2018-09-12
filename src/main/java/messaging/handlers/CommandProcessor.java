package messaging.handlers;

import com.lmax.disruptor.EventHandler;
import domain.auction.service.AuctionService;
import messaging.CommandBase;

import java.util.concurrent.CountDownLatch;

public class CommandProcessor implements EventHandler<CommandBase> {

    private final AuctionService auctionService;
    private CountDownLatch latch;

    public CommandProcessor(AuctionService auctionService, CountDownLatch latch) {

        this.auctionService = auctionService;
        this.latch = latch;

    }

    @Override
    public void onEvent(CommandBase commandBase, long sequence, boolean endOfBatch) {

        try {

            auctionService.processCommand(commandBase.getCommand());

        } catch (Exception ex) {

            ex.printStackTrace();
            System.out.println(commandBase.getRawMessage());

        } finally {
            if (latch != null)
                latch.countDown();
        }
    }

    public void setLatch(CountDownLatch latch) {

        this.latch = latch;

    }
}

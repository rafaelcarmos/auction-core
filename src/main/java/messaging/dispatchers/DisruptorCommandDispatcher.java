package messaging.dispatchers;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import domain.auction.service.AuctionService;
import messaging.CommandBase;
import messaging.CommandDispatcher;
import messaging.handlers.CommandJournaler;
import messaging.handlers.CommandParser;
import messaging.handlers.CommandProcessor;

import java.util.concurrent.Executors;

public class DisruptorCommandDispatcher implements CommandDispatcher {

    private final Disruptor<CommandBase> disruptor;
    private final AuctionService auctionService;

    public DisruptorCommandDispatcher(AuctionService auctionService, int bufferSize) throws Exception {

        this.auctionService = auctionService;
        disruptor = new Disruptor<>(CommandBase::new, bufferSize, Executors.defaultThreadFactory(), ProducerType.SINGLE, new BusySpinWaitStrategy());
        disruptor.handleEventsWith(new CommandJournaler(), new CommandParser()).then(new CommandProcessor(auctionService));
        disruptor.start();
    }

    @Override
    public void processCommand(String rawMessage) {
        long seq = disruptor.getRingBuffer().next();
        CommandBase commandBase = disruptor.getRingBuffer().claimAndGetPreallocated(seq);
        commandBase.setRawMessage(rawMessage);
        disruptor.getRingBuffer().publish(seq);
    }

    @Override
    public void shutdown() {
        disruptor.shutdown();
        auctionService.close();
    }
}

package messaging.disruptor;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import domain.auction.AuctionService;
import domain.auction.AuctionServiceImpl;
import domain.auction.repository.Repository;
import messaging.CommandBase;
import messaging.CommandDispatcher;
import messaging.handlers.CommandParser;

import java.util.concurrent.Executors;

public class DisruptorCommandDispatcher implements CommandDispatcher {

    private final Disruptor<CommandBase> disruptor;
    private final Repository repository;
    private final AuctionService auctionService;

    public DisruptorCommandDispatcher(Repository repository, int bufferSize) {
        this.repository = repository;
        auctionService = new AuctionServiceImpl(repository);
        disruptor = new Disruptor<>(CommandBase::new, bufferSize, Executors.defaultThreadFactory(), ProducerType.MULTI, new BusySpinWaitStrategy());
        disruptor.handleEventsWith(new CommandParser(repository, auctionService));
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
        repository.close();
    }
}

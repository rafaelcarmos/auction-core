package command.dispatcher;

import com.google.gson.JsonObject;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import command.dispatcher.handlers.CommandProcessor;
import command.repository.Repository;
import command.service.AuctionService;
import command.service.AuctionServiceImpl;

import java.util.concurrent.Executors;

public class DisruptorCommandDispatcher implements CommandDispatcher {

    private final Disruptor<CommandBase> disruptor;
    private final Repository repository;
    private final AuctionService auctionService;

    public DisruptorCommandDispatcher(Repository repository, int bufferSize) {
        this.repository = repository;
        auctionService = new AuctionServiceImpl(repository);
        disruptor = new Disruptor<>(CommandBase::new, bufferSize, Executors.defaultThreadFactory(), ProducerType.MULTI, new BusySpinWaitStrategy());
        disruptor.handleEventsWith(new CommandProcessor(repository, auctionService));
        disruptor.start();
    }

    @Override
    public void processCommand(JsonObject rawMessage) {
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

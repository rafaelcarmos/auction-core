package command.dispatcher;

import com.google.gson.JsonObject;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import command.dispatcher.handlers.CommandParser;
import command.dispatcher.handlers.CommandProcessor;
import command.repository.Repository;
import command.service.AuctionService;
import command.service.AuctionServiceImpl;

import java.util.concurrent.Executors;

public class DisruptorCommandDispatcher implements CommandDispatcher {

    private final Disruptor<CommandBase> disruptor;
    private final Repository repository;
    private final AuctionService auctionService;

    private long currentSeq = -1;

    public DisruptorCommandDispatcher(Repository repository, int bufferSize) {
        this.repository = repository;
        auctionService = new AuctionServiceImpl(repository);
        disruptor = new Disruptor<>(CommandBase::new, bufferSize, Executors.defaultThreadFactory(), ProducerType.SINGLE, new BusySpinWaitStrategy());
        disruptor.handleEventsWith(new CommandParser()).then(new CommandProcessor(repository, auctionService));
        disruptor.start();
    }

    public Disruptor<CommandBase> getDisruptor() {
        return disruptor;
    }

    public long getCurrentSeq() {
        return currentSeq;
    }

    @Override
    public void processCommand(JsonObject rawMessage) {
        currentSeq = disruptor.getRingBuffer().next();
        CommandBase commandBase = disruptor.getRingBuffer().claimAndGetPreallocated(currentSeq);
        commandBase.setRawMessage(rawMessage);
        disruptor.getRingBuffer().publish(currentSeq);
    }

    @Override
    public void shutdown() {
        disruptor.shutdown();
    }
}

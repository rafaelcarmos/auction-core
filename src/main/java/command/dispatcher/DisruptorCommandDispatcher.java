package command.dispatcher;

import com.google.gson.JsonObject;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import command.dispatcher.handlers.CommandParser;
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
        disruptor = new Disruptor<>(CommandBase::new, bufferSize, Executors.defaultThreadFactory(), ProducerType.SINGLE, new BusySpinWaitStrategy());
        disruptor.handleEventsWith(new CommandParser(auctionService));
        disruptor.start();
    }

    @Override
    public void processCommand(JsonObject rawMessage) {
        disruptor.publishEvent((c,  o) -> c.setRawMessage(rawMessage));
    }

    @Override
    public void shutdown() {
        disruptor.shutdown();
    }
}

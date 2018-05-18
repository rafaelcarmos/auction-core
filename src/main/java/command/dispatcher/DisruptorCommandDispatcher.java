package command.dispatcher;

import com.lmax.disruptor.dsl.Disruptor;
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

    public DisruptorCommandDispatcher(Repository repository, int bufferSize) throws Exception {
        this.repository = repository;
        this.auctionService = new AuctionServiceImpl(repository);
        auctionService.replayAllEvents();
        this.disruptor = new Disruptor<>(CommandBase::new, bufferSize, Executors.defaultThreadFactory());
        this.disruptor.handleEventsWith(new CommandParser()).then(new CommandProcessor(repository, auctionService));
        disruptor.start();
    }

    @Override
    public void processCommand(String rawMessage) {
        disruptor.publishEvent((c,  o) -> c.setRawMessage(rawMessage));
    }

    @Override
    public void shutdown() {
        disruptor.shutdown();
    }
}

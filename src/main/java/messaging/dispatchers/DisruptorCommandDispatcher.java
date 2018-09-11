package messaging.dispatchers;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventTranslatorOneArg;
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

    private final EventTranslatorOneArg<CommandBase, String> translator = (event, sequence, rawMessage) -> event.setRawMessage(rawMessage);
    private final Disruptor<CommandBase> disruptor;
    private final AuctionService auctionService;
    private final CommandJournaler journaler;
    private final CommandParser parser;
    private final CommandProcessor processor;

    public DisruptorCommandDispatcher(AuctionService auctionService, int bufferSize) throws Exception {

        this.auctionService = auctionService;

        journaler = new CommandJournaler();
        parser = new CommandParser();
        processor = new CommandProcessor(auctionService);

        disruptor = new Disruptor<>(CommandBase::new, bufferSize, Executors.privilegedThreadFactory(), ProducerType.SINGLE, new BusySpinWaitStrategy());
        disruptor.handleEventsWith(new CommandJournaler(), new CommandParser()).then(processor);
        disruptor.start();
    }

    @Override
    public void processCommand(String rawMessage) {
        disruptor.publishEvent(translator, rawMessage);
    }

    @Override
    public void shutdown() {

        disruptor.shutdown();
        auctionService.close();

    }
}

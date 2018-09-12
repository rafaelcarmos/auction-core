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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class DisruptorCommandDispatcher extends CommandDispatcher {

    private final EventTranslatorOneArg<CommandBase, String> translator = (event, sequence, rawMessage) -> event.setRawMessage(rawMessage);
    private final Disruptor<CommandBase> disruptor;

    public DisruptorCommandDispatcher(AuctionService auctionService, int size, CountDownLatch latch) throws Exception {

        super(auctionService, size, latch);

        disruptor = new Disruptor<>(CommandBase::new, size, Executors.privilegedThreadFactory(), ProducerType.SINGLE, new BusySpinWaitStrategy());
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

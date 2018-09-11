package messaging.dispatchers;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import domain.auction.service.AuctionService;
import messaging.CommandBase;
import messaging.CommandDispatcher;
import messaging.handlers.SingleThreadedCommandHandler;

import java.util.concurrent.Executors;

public class SingleThreadedDisruptorCommandDispatcher implements CommandDispatcher {

    private final EventTranslatorOneArg<CommandBase, String> translator = (event, sequence, rawMessage) -> event.setRawMessage(rawMessage);

    private final Disruptor<CommandBase> disruptor;
    private final AuctionService auctionService;
    private final SingleThreadedCommandHandler handler;

    public SingleThreadedDisruptorCommandDispatcher(AuctionService auctionService, int bufferSize) throws Exception {

        this.auctionService = auctionService;
        disruptor = new Disruptor<>(CommandBase::new, bufferSize, Executors.defaultThreadFactory(), ProducerType.SINGLE, new BusySpinWaitStrategy());
        handler = new SingleThreadedCommandHandler(auctionService);
        disruptor.handleEventsWith(handler);
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

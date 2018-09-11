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

    private final EventTranslatorOneArg<CommandBase, String> translator = new EventTranslatorOneArg<CommandBase, String>() {
        @Override
        public void translateTo(CommandBase event, long sequence, String arg0) {
            event.setRawMessage(arg0);
        }
    };

    private final Disruptor<CommandBase> disruptor;
    private final AuctionService auctionService;

    public SingleThreadedDisruptorCommandDispatcher(AuctionService auctionService, int bufferSize) throws Exception {

        this.auctionService = auctionService;
        disruptor = new Disruptor<>(CommandBase::new, bufferSize, Executors.defaultThreadFactory(), ProducerType.SINGLE, new BusySpinWaitStrategy());
        disruptor.handleEventsWith(new SingleThreadedCommandHandler(auctionService));
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

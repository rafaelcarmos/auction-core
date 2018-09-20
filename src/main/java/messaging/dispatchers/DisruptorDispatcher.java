package messaging.dispatchers;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import domain.auction.service.AuctionService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class DisruptorDispatcher extends CommandDispatcher {

    private final EventTranslatorOneArg<CommandBase, String> translator = (event, sequence, rawMessage) -> event.setRawMessage(rawMessage);
    private final Disruptor<CommandBase> disruptor;

    public DisruptorDispatcher(AuctionService auctionService, int size, CountDownLatch latch) throws Exception {

        super(auctionService, size, latch);

        disruptor = new Disruptor<>(CommandBase::new, size, Executors.privilegedThreadFactory(), ProducerType.SINGLE, new BusySpinWaitStrategy());
        disruptor.handleEventsWith(journaler, parser).then(processor);
        disruptor.start();
    }

    @Override
    public void processCommand(String rawMessage) {
        disruptor.publishEvent(translator, rawMessage);
    }

    @Override
    public void shutdown() {
        try {
            disruptor.shutdown();
            auctionService.close();
            journaler.close();
        } catch (Exception ex) {

        }
    }
}

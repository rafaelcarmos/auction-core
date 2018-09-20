package messaging.dispatchers;

import domain.auction.service.AuctionService;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

public class ArrayBlockingQueueDispatcher extends BaseQueueDispatcher {

    private boolean stopped = false;

    public ArrayBlockingQueueDispatcher(AuctionService auctionService, int size, CountDownLatch latch) throws Exception {
        super(auctionService, size, latch);
    }

    @Override
    protected void InitializeQueues() {

        this.journalerInputQueue = new ArrayBlockingQueue<>(size);
        this.parserInputQueue = new ArrayBlockingQueue<>(size);
        this.journalerOutputQueue = new ArrayBlockingQueue<>(size);
        this.parserOutputQueue = new ArrayBlockingQueue<>(size);
    }
}

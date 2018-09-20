package messaging.dispatchers;

import domain.auction.service.AuctionService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class LinkedBlockingQueueDispatcher extends BaseQueueDispatcher {

    private boolean stopped = false;

    public LinkedBlockingQueueDispatcher(AuctionService auctionService, int size, CountDownLatch latch) throws Exception {
        super(auctionService, size, latch);
    }

    @Override
    protected void InitializeQueues() {

        this.journalerInputQueue = new LinkedBlockingQueue<>(size);
        this.parserInputQueue = new LinkedBlockingQueue<>(size);
        this.journalerOutputQueue = new LinkedBlockingQueue<>(size);
        this.parserOutputQueue = new LinkedBlockingQueue<>(size);
    }
}

package messaging.dispatchers;

import domain.auction.service.AuctionService;
import messaging.CommandBase;
import messaging.CommandDispatcher;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseQueueDispatcher extends CommandDispatcher {

    private final ExecutorService executor;
    protected BlockingQueue<CommandBase> mainInputQueue;
    protected BlockingQueue<CommandBase> journalerInputQueue;
    protected BlockingQueue<CommandBase> parserInputQueue;
    protected BlockingQueue<CommandBase> journalerOutputQueue;
    protected BlockingQueue<CommandBase> parserOutputQueue;
    private boolean stopped = false;

    public BaseQueueDispatcher(AuctionService auctionService, int size, CountDownLatch latch) throws Exception {

        super(auctionService, size, latch);

        InitializeQueues();

        this.executor = Executors.newCachedThreadPool();

        this.executor.submit(this::consumeMainInputQueue);
        this.executor.submit(this::consumeJournalerInputQueue);
        this.executor.submit(this::consumeParserInputQueue);
        this.executor.submit(this::consumeParserAndJournalerOuputQueue);
    }

    protected abstract void InitializeQueues();

    @Override
    public void processCommand(String rawMessage) throws Exception {

        CommandBase cmd = new CommandBase();
        cmd.setRawMessage(rawMessage);
        mainInputQueue.put(cmd);
    }

    private void consumeMainInputQueue() {

        while (!stopped) {

            try {

                while (!mainInputQueue.isEmpty()) {

                    CommandBase cmd = mainInputQueue.take();

                    journalerInputQueue.put(cmd);
                    parserInputQueue.put(cmd);
                }
            } catch (Exception ex) {

            }
        }
    }

    private void consumeJournalerInputQueue() {

        while (!stopped) {

            try {

                while (!journalerInputQueue.isEmpty()) {

                    CommandBase cmd = journalerInputQueue.take();

                    journaler.onEvent(cmd, -1, journalerInputQueue.isEmpty());

                    journalerOutputQueue.put(cmd);
                }
            } catch (Exception ex) {

            }
        }
    }

    private void consumeParserInputQueue() {

        while (!stopped) {

            try {

                while (!parserInputQueue.isEmpty()) {

                    CommandBase cmd = parserInputQueue.take();

                    parser.onEvent(cmd, -1, parserInputQueue.isEmpty());

                    parserOutputQueue.put(cmd);
                }
            } catch (Exception ex) {

            }
        }
    }

    private void consumeParserAndJournalerOuputQueue() {

        while (!stopped) {

            try {

                while (!journalerOutputQueue.isEmpty() && !parserOutputQueue.isEmpty()) {

                    journalerOutputQueue.take();
                    CommandBase cmdParser = parserOutputQueue.take();

                    processor.onEvent(cmdParser, -1, journalerOutputQueue.isEmpty() && parserOutputQueue.isEmpty());
                }
            } catch (Exception ex) {

            }
        }
    }

    @Override
    public void shutdown() {

        stopped = true;

        try {

            executor.shutdownNow();
            auctionService.close();
            journaler.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

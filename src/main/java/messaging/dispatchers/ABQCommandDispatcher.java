package messaging.dispatchers;

import domain.auction.service.AuctionService;
import messaging.CommandBase;
import messaging.CommandDispatcher;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ABQCommandDispatcher extends CommandDispatcher {

    private final ArrayBlockingQueue<CommandBase> mainInputQueue;
    private final ArrayBlockingQueue<CommandBase> journalerInputQueue;
    private final ArrayBlockingQueue<CommandBase> parserInputQueue;
    private final ArrayBlockingQueue<CommandBase> journalerOutputQueue;
    private final ArrayBlockingQueue<CommandBase> parserOutputQueue;

    private final ExecutorService executor;

    private boolean stopped = false;

    public ABQCommandDispatcher(AuctionService auctionService, int size, CountDownLatch latch) throws Exception {

        super(auctionService, size, latch);

        this.executor = Executors.newCachedThreadPool();

        this.mainInputQueue = new ArrayBlockingQueue<>(size);
        this.journalerInputQueue = new ArrayBlockingQueue<>(size);
        this.parserInputQueue = new ArrayBlockingQueue<>(size);
        this.journalerOutputQueue = new ArrayBlockingQueue<>(size);
        this.parserOutputQueue = new ArrayBlockingQueue<>(size);

        this.executor.submit(this::consumeMainInputQueue);
        this.executor.submit(this::consumeJournalerInputQueue);
        this.executor.submit(this::consumeParserInputQueue);
        this.executor.submit(this::consumeParserAndJournalerOuputQueue);
    }

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

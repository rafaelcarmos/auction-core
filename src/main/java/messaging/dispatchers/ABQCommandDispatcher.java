package messaging.dispatchers;

import domain.auction.service.AuctionService;
import messaging.CommandBase;
import messaging.CommandDispatcher;
import messaging.handlers.CommandJournaler;
import messaging.handlers.CommandParser;
import messaging.handlers.CommandProcessor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ABQCommandDispatcher implements CommandDispatcher {

    private final ArrayBlockingQueue<CommandBase> mainInputQueue;
    private final ArrayBlockingQueue<CommandBase> journalerInputQueue;
    private final ArrayBlockingQueue<CommandBase> parserInputQueue;
    private final ArrayBlockingQueue<CommandBase> journalerOutputQueue;
    private final ArrayBlockingQueue<CommandBase> parserOutputQueue;
    private final ExecutorService executor;
    private final AuctionService auctionService;
    private final CommandJournaler journaler;
    private final CommandParser parser;
    private final CommandProcessor processor;
    private boolean stopped = false;

    public ABQCommandDispatcher(AuctionService auctionService, int queueSize) throws Exception {

        this.executor = Executors.newCachedThreadPool();
        this.auctionService = auctionService;

        journaler = new CommandJournaler();
        parser = new CommandParser();
        processor = new CommandProcessor(auctionService);

        mainInputQueue = new ArrayBlockingQueue<>(queueSize);
        journalerInputQueue = new ArrayBlockingQueue<>(queueSize);
        parserInputQueue = new ArrayBlockingQueue<>(queueSize);
        journalerOutputQueue = new ArrayBlockingQueue<>(queueSize);
        parserOutputQueue = new ArrayBlockingQueue<>(queueSize);

        executor.submit(this::consumeMainInputQueue);
        executor.submit(this::consumeJournalerInputQueue);
        executor.submit(this::consumeParserInputQueue);
        executor.submit(this::consumeParserAndJournalerOuputQueue);
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

                    journaler.onEvent(cmd, -1, false);

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

                    parser.onEvent(cmd, -1, false);

                    parserOutputQueue.put(cmd);
                }

            } catch (Exception ex) {

            }
        }
    }

    private void consumeParserAndJournalerOuputQueue() {

        while (!stopped) {

            try {

                CommandBase cmdJournaler = journalerOutputQueue.take();
                CommandBase cmdParser = parserOutputQueue.take();

                if (cmdJournaler != cmdParser)
                    throw new Exception("Unsynchronized queues");

                processor.onEvent(cmdJournaler, -1, false);

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

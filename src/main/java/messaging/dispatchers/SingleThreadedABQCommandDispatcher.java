package messaging.dispatchers;

import domain.auction.service.AuctionService;
import messaging.CommandBase;
import messaging.CommandDispatcher;
import messaging.handlers.SingleThreadedCommandHandler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleThreadedABQCommandDispatcher implements CommandDispatcher {

    private final ArrayBlockingQueue<CommandBase> mainInputQueue;
    private final ExecutorService executor;
    private final AuctionService auctionService;
    private final SingleThreadedCommandHandler handler;
    private boolean stopped = false;
    private long sequence = 0;

    public SingleThreadedABQCommandDispatcher(AuctionService auctionService, int queueSize) throws Exception {

        this.executor = Executors.newCachedThreadPool();
        this.auctionService = auctionService;

        handler = new SingleThreadedCommandHandler(auctionService);

        mainInputQueue = new ArrayBlockingQueue<>(queueSize);

        executor.submit(this::consumeMainInputQueue);
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
                    handler.onEvent(cmd, sequence++, mainInputQueue.isEmpty());
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
            handler.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

package messaging.dispatchers;

import domain.auction.service.AuctionService;
import messaging.CommandBase;
import messaging.CommandDispatcher;
import messaging.handlers.CommandParser;
import test.ShutdownCommand;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;

public class ABQCommandDispatcher implements CommandDispatcher {

    private final ArrayBlockingQueue<CommandBase> queue;
    private final AuctionService auctionService;
    private Thread consumer;
    private boolean stopped = false;

    public ABQCommandDispatcher(AuctionService auctionService, int queueSize) {
        this.auctionService = auctionService;
        queue = new ArrayBlockingQueue<>(queueSize);
        consumer = Executors.defaultThreadFactory().newThread(this::consumeQueue);
        consumer.start();
    }

    @Override
    public void processCommand(String rawMessage) throws Exception {
        CommandBase cmd = new CommandBase();
        cmd.setRawMessage(rawMessage);
        queue.put(cmd);
    }

    private void consumeQueue() {
        CommandParser processor = new CommandParser();
        CommandBase cmd;

        while (!stopped) {
            try {
                cmd = queue.take();

                //POISON MESSAGE, STOP CONSUMING
                if (cmd.getCommand() instanceof ShutdownCommand)
                    return;

                if (cmd != null) {
                    processor.onEvent(cmd, 0, false);
                }

            } catch (Exception ex) {

            }
        }
    }

    @Override
    public void shutdown() {
        stopped = true;
        try {

            CommandBase poisonMessage = new CommandBase();
            poisonMessage.setCommand(new ShutdownCommand(null, null));

            //Kill consumer thread
            queue.put(poisonMessage);

            consumer.join();
            auctionService.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

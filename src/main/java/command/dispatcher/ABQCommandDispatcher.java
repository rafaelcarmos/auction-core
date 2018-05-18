package command.dispatcher;

import command.dispatcher.handlers.CommandParser;
import command.dispatcher.handlers.CommandProcessor;
import command.repository.Repository;
import command.service.AuctionService;
import command.service.AuctionServiceImpl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;

public class ABQCommandDispatcher implements CommandDispatcher {

    private final ArrayBlockingQueue<CommandBase> queue;
    private final Repository repository;
    private final AuctionService auctionService;
    private Thread consumer;
    private boolean stopped = false;

    public ABQCommandDispatcher(Repository repository, int queueSize) throws Exception {
        this.repository = repository;
        this.auctionService = new AuctionServiceImpl(repository);
        auctionService.replayAllEvents();
        this.queue = new ArrayBlockingQueue<CommandBase>(queueSize);

        consumer = Executors.defaultThreadFactory().newThread(() -> {
            ConsumeQueue();
        });

        consumer.start();
    }

    @Override
    public void processCommand(String rawMessage) throws Exception {
        CommandBase cmd = new CommandBase();
        cmd.setRawMessage(rawMessage);
        queue.put(cmd);
    }

    @Override
    public void shutdown() {
        stopped = true;
        try {
            consumer.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void ConsumeQueue() {
        CommandParser parser = new CommandParser();
        CommandProcessor processor = new CommandProcessor(repository, auctionService);
        CommandBase cmd = null;
        while (!stopped || !queue.isEmpty()) {
            cmd = queue.poll();
            if (cmd != null) {
                parser.onEvent(cmd, 0, true);
                processor.onEvent(cmd, 0, true);
            }
        }
    }
}

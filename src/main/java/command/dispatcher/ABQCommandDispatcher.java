package command.dispatcher;

import com.google.gson.JsonObject;
import command.dispatcher.handlers.CommandParser;
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

    public ABQCommandDispatcher(Repository repository, int queueSize) {
        this.repository = repository;
        auctionService = new AuctionServiceImpl(repository);
        queue = new ArrayBlockingQueue<>(queueSize);
        consumer = Executors.defaultThreadFactory().newThread(this::ConsumeQueue);
        consumer.start();
    }

    @Override
    public void processCommand(JsonObject rawMessage) throws Exception {
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
        CommandParser parser = new CommandParser(auctionService);
        CommandBase cmd;

        while (!stopped || !queue.isEmpty()) {
            cmd = queue.poll();
            if (cmd != null) {
                parser.onEvent(cmd, 0, true);
            }
        }
    }
}

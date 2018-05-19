package test;

import com.google.gson.JsonObject;
import command.dispatcher.DisruptorCommandDispatcher;
import command.repository.Repository;

import java.util.List;
import java.util.concurrent.locks.LockSupport;

public class TestDisruptor extends TestBase {

    public TestDisruptor() {

    }

    @Override
    public void run(int commandsSize, Repository repository) {
        try {
            List<JsonObject> commands = getCommands(commandsSize);
            DisruptorCommandDispatcher dispatcher = new DisruptorCommandDispatcher(repository, (int) Math.pow(2, 16));

            final long t1 = System.nanoTime();
            final long t2;

            for (JsonObject o : commands)
                dispatcher.processCommand(o);


            //ScheduledExecutorService exc = Executors.newSingleThreadScheduledExecutor();
            //exc.scheduleAtFixedRate(())

            Thread watcher = new Thread(() -> {
                try {
                    while (dispatcher.getCurrentSeq() < commandsSize - 1) {
                        LockSupport.parkNanos(10000);
                    }
                    System.out.println("[Disruptor][size= " + commandsSize + "] Elapsed Time: " + (System.nanoTime() - t1));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            watcher.start();
            watcher.join();
            dispatcher.shutdown();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

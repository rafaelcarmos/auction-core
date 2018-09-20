package test;

import messaging.dispatchers.CommandDispatcher;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ThroughputBenchmark implements BenchmarkBase {

    private double commandsPerMillisecond = -1;

    @Override
    public void run(CommandDispatcher dispatcher, String command, int batchSize) throws Exception {

        System.out.println(dispatcher.getClass().getSimpleName());

        CountDownLatch latch = new CountDownLatch(batchSize);
        dispatcher.setLatch(latch);

        long timerStart = System.nanoTime();

        for (int commandCount = 0; commandCount < batchSize; commandCount++)
            dispatcher.processCommand(command);

        long timerEnd = System.nanoTime();
        long elapsedTimeNano = timerEnd - timerStart;
        double elapsedMilli = TimeUnit.NANOSECONDS.toMillis(elapsedTimeNano);

        commandsPerMillisecond = batchSize / elapsedMilli;

        latch.await();

        System.gc();
    }

    public double getCommandsPerMillisecond() {
        return commandsPerMillisecond;
    }
}

package test;

import messaging.CommandDispatcher;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ThroughputBenchmark implements BenchmarkBase {

    private double commandsPerMillisecond = -1;

    @Override
    public void run(CommandDispatcher dispatcher, String command, int totalCommands) throws Exception {

        CountDownLatch latch = new CountDownLatch(totalCommands);
        dispatcher.setLatch(latch);

        long timerStart = System.nanoTime();

        for (int commandCount = 0; commandCount < totalCommands; commandCount++)
            dispatcher.processCommand(command);

        long timerEnd = System.nanoTime();
        long elapsedTimeNano = timerEnd - timerStart;
        double elapsedMilli = TimeUnit.NANOSECONDS.toMillis(elapsedTimeNano);

        commandsPerMillisecond = totalCommands / elapsedMilli;

        latch.await();

        System.gc();
    }

    public double getCommandsPerMillisecond() {
        return commandsPerMillisecond;
    }
}

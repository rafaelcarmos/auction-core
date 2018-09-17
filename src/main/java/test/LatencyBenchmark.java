package test;

import messaging.CommandDispatcher;

import java.util.concurrent.CountDownLatch;

public class LatencyBenchmark implements BenchmarkBase {

    private double meanLatency = -1;

    @Override
    public void run(CommandDispatcher dispatcher, String command, int totalCommands) throws Exception {

        CountDownLatch latch = new CountDownLatch(totalCommands);
        dispatcher.setLatch(latch);

        long timerStart = System.nanoTime();

        for (int commandCount = 0; commandCount < totalCommands; commandCount++) {
            dispatcher.processCommand(command);
        }

        latch.await();

        long timerEnd = System.nanoTime();
        long elapsedTimeNano = timerEnd - timerStart;
        meanLatency = elapsedTimeNano / totalCommands;

        dispatcher.setLatch(null);

        System.gc();
    }

    public double getMeanLatency() {
        return meanLatency;
    }
}

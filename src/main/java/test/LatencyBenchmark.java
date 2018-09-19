package test;

import messaging.CommandDispatcher;

import java.util.concurrent.CountDownLatch;

public class LatencyBenchmark implements BenchmarkBase {

    private double meanLatency = -1;

    @Override
    public void run(CommandDispatcher dispatcher, String command, int iterations) throws Exception {

        CountDownLatch latch = new CountDownLatch(iterations);
        dispatcher.setLatch(latch);

        long timerStart = System.nanoTime();

        for (int commandCount = 0; commandCount < iterations; commandCount++) {
            dispatcher.processCommand(command);
        }

        latch.await();

        long timerEnd = System.nanoTime();
        long elapsedTimeNano = timerEnd - timerStart;
        meanLatency = elapsedTimeNano / iterations;

        dispatcher.setLatch(null);

        System.gc();
    }

    public double getMeanLatency() {
        return meanLatency;
    }
}

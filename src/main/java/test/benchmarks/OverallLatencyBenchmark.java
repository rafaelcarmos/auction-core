package test.benchmarks;

import messaging.dispatchers.CommandDispatcher;

import java.util.concurrent.CountDownLatch;

public class OverallLatencyBenchmark implements BenchmarkBase {

    private double averageLatency = -1;

    @Override
    public void run(CommandDispatcher dispatcher, byte[] command, int iterations) throws Exception {

        System.gc();

        CountDownLatch latch = new CountDownLatch(iterations);
        dispatcher.setLatch(latch);

        long timerStart = System.nanoTime();

        for (int commandCount = 0; commandCount < iterations; commandCount++) {
            dispatcher.processCommand(command);
        }

        latch.await();

        long timerEnd = System.nanoTime();
        long elapsedTimeNano = timerEnd - timerStart;
        averageLatency = elapsedTimeNano / iterations;

        dispatcher.setLatch(null);
    }

    public double getAverageLatency() {
        return averageLatency;
    }
}

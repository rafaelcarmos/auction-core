package test;

import messaging.dispatchers.CommandDispatcher;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class IndividualLatencyBenchmark implements BenchmarkBase {

    private long minLatency = -1;
    private long maxLatency = -1;
    private long medianLatency = -1;
    private long ninetyNinePercentBelow = -1;


    @Override
    public void run(CommandDispatcher dispatcher, String command, int iterations) throws Exception {

        CountDownLatch latch = new CountDownLatch(1);
        dispatcher.setLatch(latch);

        long[] latencies = new long[iterations];
        for (int commandCount = 0; commandCount < iterations; commandCount++) {

            long timerStart = System.nanoTime();

            dispatcher.processCommand(command);

            latch.await();

            long timerEnd = System.nanoTime();
            long elapsedTimeNano = timerEnd - timerStart;
            latencies[commandCount] = elapsedTimeNano;

            latch = new CountDownLatch(1);
            dispatcher.setLatch(latch);
        }

        int maxIndex = 0;
        for (int i = 0; i < latencies.length; i++) {
            if (latencies[i] >= latencies[maxIndex])
                maxIndex = i;
        }

        System.out.format("%s | Max: %d | Index: %d \n", dispatcher.getClass().getSimpleName(), latencies[maxIndex], maxIndex);

        Arrays.sort(latencies);

        int ninetyNinePercent = (int) (iterations * 99.9) / 100;

        minLatency = latencies[0];
        maxLatency = latencies[iterations - 1];
        ninetyNinePercentBelow = latencies[ninetyNinePercent];
        medianLatency = latencies[iterations / 2];

        System.gc();
    }

    public long getNinetyNinePercentBelow() {
        return ninetyNinePercentBelow;
    }

    public long getMedianLatency() {
        return medianLatency;
    }

    public long getMinLatency() {
        return minLatency;
    }

    public long getMaxLatency() {
        return maxLatency;
    }
}

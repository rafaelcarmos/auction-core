package test;

import messaging.CommandDispatcher;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class IndividualLatencyBenchmark implements BenchmarkBase {

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

        Arrays.sort(latencies);

        int ninetyNinePercent = (iterations * 99) / 100;

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
}

package test;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class IterationResults implements Comparable<IterationResults> {

    private final int producers;
    private final long nanoTime;
    private final int size;

    public IterationResults(int producers, long nanoTime, int size) {
        this.producers = producers;
        this.nanoTime = nanoTime;
        this.size = size;
    }

    public double getThroughput() {
        return size / ((double) nanoTime / 1000000000.0);
    }

    public int getProducers() {
        return producers;
    }

    public long getNanoTime() {
        return nanoTime;
    }

    public long getMilliTime() {
        return TimeUnit.MILLISECONDS.convert(nanoTime, TimeUnit.NANOSECONDS);
    }

    public int getSize() {
        return size;
    }

    @Override
    public int compareTo(@NotNull IterationResults o) {
        return Long.compare(nanoTime, o.nanoTime);
    }
}

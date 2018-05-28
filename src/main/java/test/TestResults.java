package test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestResults {

    private List<IterationResults> iterations = new ArrayList<>();
    private String type;

    public TestResults(String type) {
        this.type = type;
    }

    public List<IterationResults> getIterations() {
        return iterations;
    }

    public String getType() {
        return type;
    }

    public void addIteration(IterationResults i) {
        iterations.add(i);
    }

    public double getAverageThroughput() {
        return iterations.stream().collect(Collectors.averagingDouble(i -> i.getThroughput()));
    }

    public double getAverageNanoTime() {
        return iterations.stream().collect(Collectors.averagingLong(i -> i.getNanoTime()));
    }

    public double getAverageMilliTime() {
        return iterations.stream().collect(Collectors.averagingLong(i -> i.getMilliTime()));
    }

    public double getMedianThroughput() {
        List<IterationResults> sorted = iterations.stream().sorted().collect(Collectors.toList());
        return sorted.size() % 2 == 0 ? ((sorted.get(sorted.size() / 2).getThroughput() + sorted.get((sorted.size() / 2) + 1).getThroughput()) / 2)
                : sorted.get((sorted.size() / 2) + 1).getThroughput();
    }

    public long getMedianNanoTime() {
        List<IterationResults> sorted = iterations.stream().sorted().collect(Collectors.toList());
        return sorted.size() % 2 == 0 ? ((sorted.get(sorted.size() / 2).getNanoTime() + sorted.get((sorted.size() / 2) + 1).getNanoTime()) / 2)
                : sorted.get((sorted.size() / 2) + 1).getNanoTime();
    }

    public long getMedianMilliTime() {
        List<IterationResults> sorted = iterations.stream().sorted().collect(Collectors.toList());
        return sorted.size() % 2 == 0 ? ((sorted.get(sorted.size() / 2).getMilliTime() + sorted.get((sorted.size() / 2) + 1).getMilliTime()) / 2)
                : sorted.get((sorted.size() / 2) + 1).getMilliTime();
    }

    public String printTest() {

        Columns cols = new Columns();
        cols.addLine("#", "NANO", "MILLI", "THROUGHPUT");

        int colIndex = 0;
        for (IterationResults i : iterations) {
            cols.addLine(String.valueOf(colIndex), String.valueOf(i.getNanoTime()), String.valueOf(i.getMilliTime()), String.format("%.2f/s", i.getThroughput()));
            colIndex++;
        }

        cols.addLine("MDN", String.valueOf(getMedianNanoTime()), String.valueOf(getMedianMilliTime()), String.format("%.2f/s", getMedianThroughput()));
        cols.addLine("AVG", String.format("%.2f", getAverageNanoTime()), String.format("%.2f", getAverageMilliTime()), String.format("%.2f/s", getAverageThroughput()));

        return cols.toString();
    }
}

package test;

import command.dispatcher.ABQCommandDispatcher;
import command.dispatcher.CommandDispatcher;
import command.dispatcher.DisruptorCommandDispatcher;
import command.repository.MongoRepository;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import java.util.ArrayList;
import java.util.List;

public class TestMain {

    public static void main(String args[]) {

        final int size = 1000000;
        final int sizeSteps = 10;
        final int iterations = 5;
        final int producers = 1;

        CommandDispatcher commandDispatcher = null;
        int bufferSize = (int) Math.pow(2, 16);

        List<CommandDispatcher> dispatchers = new ArrayList<>();

        dispatchers.add(new DisruptorCommandDispatcher(new MongoRepository("localhost:27017", "event-store-disruptor", "events"), bufferSize));
        dispatchers.add(new ABQCommandDispatcher(new MongoRepository("localhost:27017", "event-store-ABQ", "events"), bufferSize));

        List<XYChart> charts = run(size, sizeSteps, iterations, producers, dispatchers);

        for (XYChart chart : charts) {
            new SwingWrapper<>(chart).displayChart();
            try {
                BitmapEncoder.saveBitmap(chart, String.format("./%s.png", chart.getTitle().replace(' ', '_')), BitmapEncoder.BitmapFormat.PNG);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static List<XYChart> run(int size, int sizeSteps, int iterations, int producers, List<CommandDispatcher> dispatchers) {

        double[] xData = new double[sizeSteps];
        double[][] yDataAverageExecutionTime = new double[dispatchers.size()][sizeSteps];
        double[][] yDataAverageThroughput = new double[dispatchers.size()][sizeSteps];
        double[][] yDataMedianExecutionTime = new double[dispatchers.size()][sizeSteps];
        double[][] yDataMedianThroughput = new double[dispatchers.size()][sizeSteps];


        String[] seriesNames = new String[dispatchers.size()];

        int dispatcherIndex = 0;

        for (CommandDispatcher dispatcher : dispatchers) {

            seriesNames[dispatcherIndex] = dispatcher.getClass().getName();
            int counter = 0;

            //WARM UP DISPATCHER
            TestBase warmup = new TestBase();
            try {
                warmup.run(10000, producers, iterations, dispatcher);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }

            ///TEST ITERATIONS
            try {

                for (int step = 1; step <= sizeSteps; step++) {

                    int dividedSize = size / sizeSteps;
                    int currentSize = dividedSize * step;

                    TestBase test = new TestBase();
                    TestResults results = test.run(currentSize, producers, iterations, dispatcher);

                    System.out.println(String.format("[%s][Producers: %d][Size: %d]", results.getType(), producers, currentSize));
                    System.out.println(results.printTest());

                    xData[counter] = currentSize / 1000;
                    yDataAverageExecutionTime[dispatcherIndex][counter] = results.getAverageMilliTime();
                    yDataAverageThroughput[dispatcherIndex][counter] = results.getAverageThroughput() / 1000;
                    yDataMedianExecutionTime[dispatcherIndex][counter] = results.getMedianMilliTime();
                    yDataMedianThroughput[dispatcherIndex][counter] = results.getMedianThroughput() / 1000;
                    counter++;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                dispatcher.shutdown();
                dispatcher = null;
            }

            dispatcherIndex++;
        }

        List<XYChart> charts = new ArrayList<>();
        charts.add(QuickChart.getChart("Average Execution Time with " + producers + " producers", "Number of messages (Thousand)", "Elapsed Time (milliseconds)", seriesNames, xData, yDataAverageExecutionTime));
        charts.add(QuickChart.getChart("Average Throughput with " + producers + " producers", "Number of messages (Thousand)", "Throughput (Thousand Messages/second)", seriesNames, xData, yDataAverageThroughput));
        charts.add(QuickChart.getChart("Median Execution Time with " + producers + " producers", "Number of messages (Thousand)", "Elapsed Time (milliseconds)", seriesNames, xData, yDataMedianExecutionTime));
        charts.add(QuickChart.getChart("Median Throughput with " + producers + " producers", "Number of messages (Thousand)", "Throughput (Thousand Messages/second)", seriesNames, xData, yDataMedianThroughput));

        return charts;
    }
}

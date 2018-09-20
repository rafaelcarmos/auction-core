package test;

import domain.auction.repository.InMemoryRepository;
import domain.auction.service.AuctionServiceImpl;
import messaging.dispatchers.ArrayBlockingQueueDispatcher;
import messaging.dispatchers.CommandDispatcher;
import messaging.dispatchers.DisruptorDispatcher;
import messaging.dispatchers.LinkedBlockingQueueDispatcher;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BenchmarkLatency {

    private static final int ITERATIONS = 1000000;
    private static final int BUFFER_SIZE = (1024 * 1024) * 2;
    private static final DecimalFormat doubleFormatter = (DecimalFormat) NumberFormat.getIntegerInstance(Locale.US);

    public static void main(String[] args) {
        try {

            BenchmarkResults results = new BenchmarkResults();

            List<String> col = new ArrayList<>();
            col.add("");
            col.add("Min. Latency (nano)");
            col.add("Median Latency (nano)");
            col.add("Max. Latency (nano)");
            col.add("99.9% Below (nano)");
            results.addColumn(col);

            CommandDispatcher disruptor = new DisruptorDispatcher(new AuctionServiceImpl(new InMemoryRepository()), BUFFER_SIZE, null);

            runFor(disruptor, results);

            CommandDispatcher abq = new ArrayBlockingQueueDispatcher(new AuctionServiceImpl(new InMemoryRepository()), BUFFER_SIZE, null);

            runFor(abq, results);

            CommandDispatcher lbq = new LinkedBlockingQueueDispatcher(new AuctionServiceImpl(new InMemoryRepository()), BUFFER_SIZE, null);

            runFor(lbq, results);

            results.ExportToCSV("Latency");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void runFor(CommandDispatcher dispatcher, BenchmarkResults results) throws Exception {

        System.gc();

        BenchmarkBase test = AuctionBenchmarks.INDIVIDUAL_LATENCY.getInstance();

        String placeBid = BenchmarkUtils.PrepareDispatcherAndGetPlaceBidCommand(dispatcher);

        List<String> column = new ArrayList<>();

        column.add(dispatcher.getClass().getSimpleName());

        test.run(dispatcher, placeBid, ITERATIONS);

        column.add(doubleFormatter.format(((IndividualLatencyBenchmark) test).getMinLatency()));
        column.add(doubleFormatter.format(((IndividualLatencyBenchmark) test).getMedianLatency()));
        column.add(doubleFormatter.format(((IndividualLatencyBenchmark) test).getMaxLatency()));
        column.add(doubleFormatter.format(((IndividualLatencyBenchmark) test).getNinetyNinePercentBelow()));

        results.addColumn(column);

        dispatcher.shutdown();

        System.gc();
    }
}

package test;

import domain.auction.repository.InMemoryRepository;
import domain.auction.service.AuctionServiceImpl;
import messaging.CommandDispatcher;
import messaging.dispatchers.DisruptorDispatcher;

public class BenchmarkThroughput {

    private static final int BUFFER_SIZE = 1024 * 1024;
    private static final int TOTAL_COMMANDS = BUFFER_SIZE;
    private static final int ITERATIONS = 10;

    public static void main(String[] args) {
        try {

            BenchmarkResults results = new BenchmarkResults();

            //results.addLine("Iteration");

            for (int i = 1; i <= ITERATIONS; i++) {
                // results.addLine(Integer.toString(i));
            }

            CommandDispatcher disruptorDispatcher = new DisruptorDispatcher(new AuctionServiceImpl(new InMemoryRepository()), BUFFER_SIZE, null);
            BenchmarkBase test = AuctionBenchmarks.THROUGHPUT.getInstance();

            String placeBid = BenchmarkUtils.PrepareDispatcherAndGetPlaceBidCommand(disruptorDispatcher);

            //Warm up
            test.run(disruptorDispatcher, placeBid, TOTAL_COMMANDS);

            disruptorDispatcher.shutdown();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

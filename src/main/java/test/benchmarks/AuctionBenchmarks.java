package test.benchmarks;

public enum AuctionBenchmarks {

    OVERALL_LATENCY {
        public OverallLatencyBenchmark getInstance() {
            return new OverallLatencyBenchmark();
        }

    },

    INDIVIDUAL_LATENCY {
        public IndividualLatencyBenchmark getInstance() {
            return new IndividualLatencyBenchmark();
        }

    },

    THROUGHPUT {
        public ThroughputBenchmark getInstance() {
            return new ThroughputBenchmark();
        }

    };

    public abstract BenchmarkBase getInstance();

}

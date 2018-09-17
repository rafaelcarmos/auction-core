package test;

public enum AuctionBenchmarks {

    LATENCY {
        public LatencyBenchmark getInstance() {
            return new LatencyBenchmark();
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

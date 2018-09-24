package test.benchmarks;

import messaging.dispatchers.CommandDispatcher;

public interface BenchmarkBase {

    void run(CommandDispatcher dispatcher, byte[] command, int totalCommands) throws Exception;

}


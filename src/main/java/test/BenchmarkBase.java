package test;

import messaging.CommandDispatcher;

public interface BenchmarkBase {

    void run(CommandDispatcher dispatcher, String command, int totalCommands) throws Exception;

}


package test;

import command.dispatcher.ABQCommandDispatcher;
import command.dispatcher.CommandDispatcher;
import command.repository.MongoRepository;
import command.repository.Repository;

public class TestMainABQ {

    public static void main(String args[]) {

        final int size = 10000;
        final int iterations = 10;
        CommandDispatcher abqCommandDispatcher = null;
        Repository rep = new MongoRepository("localhost:27017", "command", "events");
        int bufferSize = (int) Math.pow(2, 16);

        try {

            abqCommandDispatcher = new ABQCommandDispatcher(rep, bufferSize);

            TestBase test = new TestBase();

            for (int i = 1; i < 33; i = i * 2) {
                test.run(size, i, iterations, rep, abqCommandDispatcher);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (abqCommandDispatcher != null)
                abqCommandDispatcher.shutdown();
        }
    }
}

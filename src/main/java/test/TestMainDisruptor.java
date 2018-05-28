package test;

import command.dispatcher.CommandDispatcher;
import command.dispatcher.DisruptorCommandDispatcher;
import command.repository.MongoRepository;
import command.repository.Repository;

public class TestMainDisruptor {

    public static void main(String args[]) {

        final int size = 10000;
        final int iterations = 10;
        CommandDispatcher disruptorCommandDispatcher = null;
        Repository rep = new MongoRepository("localhost:27017", "command", "events");
        int bufferSize = (int) Math.pow(2, 13);

        try {

            disruptorCommandDispatcher = new DisruptorCommandDispatcher(rep, bufferSize);

            TestBase test = new TestBase();

            for (int i = 1; i < 20; i = i * 2) {
                TestResults results = test.run(size, i, iterations, rep, disruptorCommandDispatcher);
                System.out.println(String.format("[%s][Producers: %d][Size: %d]", results.getType(), i, size));
                System.out.println(results.printTest());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (disruptorCommandDispatcher != null)
                disruptorCommandDispatcher.shutdown();
        }
    }
}

package test;

import command.repository.MongoRepository;
import command.repository.Repository;

public class TestMain {

    public static void main(String args[]) {

        Repository rep = new MongoRepository("localhost:27017", "command", "events");

        TestABQ abq = new TestABQ();
        abq.run(1000000, rep);

        TestDisruptor disruptor = new TestDisruptor();
        disruptor.run(1000000, rep);

    }
}

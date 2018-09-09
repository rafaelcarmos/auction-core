package test;

import domain.auction.repository.InMemoryRepository;
import domain.auction.repository.Repository;
import messaging.CommandDispatcher;
import messaging.CommandDispatcherFactory;

public class TestMain {

    public static void main(String args[]) {

        try {

            int size = 1 * 1000 * 1000;
            int iterations = 5;
            int bufferSize = (int) Math.pow(2d, 12d);

            CommandDispatcher dispatcher = null;
            Repository repository = new InMemoryRepository();

            if (args.length > 0)
                size = Integer.parseInt(args[0]);

            if (args.length > 1)
                iterations = Integer.parseInt(args[1]);

            if (args.length > 2)
                dispatcher = CommandDispatcherFactory.getDispatcher(args[2], repository, bufferSize);

            String createAuction = "0;-1;-1";
            dispatcher.processCommand(createAuction);
            String startAuction = "1;"

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

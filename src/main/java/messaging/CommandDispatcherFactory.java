package messaging;

import domain.auction.repository.Repository;
import messaging.abq.ABQCommandDispatcher;
import messaging.disruptor.DisruptorCommandDispatcher;

public class CommandDispatcherFactory {

    public static CommandDispatcher getDispatcher(String name, Repository repository, int bufferSize)
            throws Exception {

        CommandDispatcher dispatcher = null;

        switch (name.toUpperCase()) {
            case "ABQ":
                dispatcher = new ABQCommandDispatcher(repository, bufferSize);
                break;
            case "DISRUPTOR":
                dispatcher = new DisruptorCommandDispatcher(repository, bufferSize);
                break;
            default:
                throw new IllegalArgumentException("Dispatcher type not found: " + name);
        }

        return dispatcher;
    }
}

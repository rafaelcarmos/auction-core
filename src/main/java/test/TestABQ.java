package test;

import com.google.gson.JsonObject;
import command.dispatcher.ABQCommandDispatcher;
import command.repository.Repository;

import java.util.List;

public class TestABQ extends TestBase {

    public TestABQ() {

    }

    @Override
    public void run(int commandsSize, Repository repository) {
        try {
            List<JsonObject> commands = getCommands(commandsSize);
            ABQCommandDispatcher dispatcher = new ABQCommandDispatcher(repository, commandsSize);

            final long t1 = System.nanoTime();

            for (JsonObject o : commands)
                dispatcher.processCommand(o);

            dispatcher.shutdown();

            System.out.println("[ABQ][size= " + commandsSize + "] Elapsed Time: " + (System.nanoTime() - t1));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

package messaging.handlers;

import com.lmax.disruptor.EventHandler;
import domain.auction.commands.Command;
import messaging.CommandBase;

public class CommandParser implements EventHandler<CommandBase> {

    public CommandParser() {

    }

    @Override
    public void onEvent(CommandBase commandBase, long sequence, boolean endOfBatch) {

        try {

            String csv = commandBase.getRawMessage();

            Command command = Command.fromCSV(csv, System.currentTimeMillis());

            commandBase.setCommand(command);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(commandBase.getRawMessage());
        }
    }
}

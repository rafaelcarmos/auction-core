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

            commandBase.setSequence(sequence);
            String csv = commandBase.getRawMessage();
            long timestamp = System.currentTimeMillis();

            Command command = Command.fromCSV(csv, sequence, timestamp);

            commandBase.setCommand(command);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(commandBase.getRawMessage());
        }
    }
}

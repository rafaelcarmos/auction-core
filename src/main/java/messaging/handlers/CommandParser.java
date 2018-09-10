package messaging.handlers;

import com.lmax.disruptor.EventHandler;
import domain.auction.commands.Command;
import messaging.CommandBase;

import java.time.LocalDateTime;

public class CommandParser implements EventHandler<CommandBase> {

    public CommandParser() {

    }

    @Override
    public void onEvent(CommandBase commandBase, long sequence, boolean endOfBatch) {
        try {

            commandBase.setSequence(sequence);
            String csv = commandBase.getRawMessage();
            LocalDateTime timestamp = LocalDateTime.now();

            Command command = Command.fromCSV(csv, timestamp);

            commandBase.setCommand(command);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(commandBase.getRawMessage());
        }
    }
}

package messaging.handlers;

import com.lmax.disruptor.EventHandler;
import messaging.CommandBase;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommandJournaler implements EventHandler<CommandBase> {

    RandomAccessFile raf;

    public CommandJournaler() throws FileNotFoundException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        raf = new RandomAccessFile("command_journal_" + LocalDateTime.now().format(formatter), "rw");
    }

    @Override
    public void onEvent(CommandBase commandBase, long sequence, boolean endOfBatch) {
        try {

            raf.writeBytes(commandBase.getRawMessage() + System.lineSeparator());

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(commandBase.getRawMessage());
        }
    }
}

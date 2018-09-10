package messaging.handlers;

import com.lmax.disruptor.EventHandler;
import messaging.CommandBase;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;

public class CommandJournaler implements EventHandler<CommandBase> {

    RandomAccessFile raf;

    public CommandJournaler() throws FileNotFoundException {

        raf = new RandomAccessFile("randomAccessFileTest" + LocalDateTime.now().toString(), "rw");
    }

    @Override
    public void onEvent(CommandBase commandBase, long sequence, boolean endOfBatch) {
        try {

            //raf.writeBytes(commandBase.getRawMessage() + System.lineSeparator());

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(commandBase.getRawMessage());
        }
    }
}

package messaging.handlers;

import com.lmax.disruptor.EventHandler;
import messaging.CommandBase;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommandJournaler implements EventHandler<CommandBase> {

    private final int BUFFER_SIZE = 1024 * 4;

    private RandomAccessFile raf;
    private CharBuffer buffer;

    public CommandJournaler() throws FileNotFoundException {

        buffer = CharBuffer.allocate(BUFFER_SIZE);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        raf = new RandomAccessFile("F:/commandJournal_" + LocalDateTime.now().format(formatter), "rw");
    }

    @Override
    public void onEvent(CommandBase commandBase, long sequence, boolean endOfBatch) {

//        try {
//
//            if ((commandBase.getRawMessage().length() + 1) >= buffer.remaining())
//                writeBytes();
//
//            buffer.append(commandBase.getRawMessage()).append('\n');
//
//            if (endOfBatch)
//                writeBytes();
//
//        } catch (Exception ex) {
//
//            ex.printStackTrace();
//            System.out.println(commandBase.getRawMessage());
//        }
    }

    private void writeBytes() throws Exception {

        raf.writeUTF(buffer.toString());
        buffer.clear();
    }

    public void close() throws Exception {

        raf.close();
    }
}

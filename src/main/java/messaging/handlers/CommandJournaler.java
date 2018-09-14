package messaging.handlers;

import com.lmax.disruptor.EventHandler;
import messaging.CommandBase;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommandJournaler implements EventHandler<CommandBase> {

    private final int BUFFER_SIZE = 1024 * 4;
    private final byte[] lineSeparator = System.lineSeparator().getBytes();
    private RandomAccessFile raf;
    private final ByteBuffer buffer;

    public CommandJournaler() throws FileNotFoundException {

        buffer = ByteBuffer.allocate(BUFFER_SIZE);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        raf = new RandomAccessFile("F:/AuctionCoreCommandJournal/commandJournal_" + LocalDateTime.now().format(formatter), "rw");
    }

    @Override
    public void onEvent(CommandBase commandBase, long sequence, boolean endOfBatch) {
        try {

            byte[] bytes = commandBase.getRawMessage().getBytes();

            if ((bytes.length + lineSeparator.length) >= buffer.remaining())
                writeBytes();

            buffer.put(bytes).put(lineSeparator);

            if (endOfBatch)
                writeBytes();

        } catch (Exception ex) {

            ex.printStackTrace();
            System.out.println(commandBase.getRawMessage());
        }
    }

    private void writeBytes() throws Exception {
        buffer.flip();
        raf.getChannel().write(buffer);
        buffer.clear();
    }

    public void close() throws Exception {
        raf.close();
    }
}

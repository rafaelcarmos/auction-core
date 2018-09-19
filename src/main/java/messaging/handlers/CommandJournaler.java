package messaging.handlers;

import com.lmax.disruptor.EventHandler;
import messaging.CommandBase;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommandJournaler implements EventHandler<CommandBase> {

    private final int ONE_MEGA_BYTE = (1024 * 1024);
    private final int BUFFER_SIZE = ONE_MEGA_BYTE * 4;
    private final byte[] lineSeparator = System.lineSeparator().getBytes();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private RandomAccessFile raf;
    private final ByteBuffer buffer;

    public CommandJournaler() throws Exception {

        buffer = ByteBuffer.allocate(BUFFER_SIZE);
        createNewJournal();
    }

    private void createNewJournal() throws Exception {

        raf = new RandomAccessFile("F:/AuctionCoreCommandJournal/commandJournal_" + LocalDateTime.now().format(formatter), "rw");
    }

    @Override
    public void onEvent(CommandBase commandBase, long sequence, boolean endOfBatch) {
        try {

            byte[] bytes = commandBase.getRawMessage().getBytes();

            if ((bytes.length + lineSeparator.length) >= buffer.remaining())
                writeBytes();

            buffer.put(bytes).put(lineSeparator);

            if (endOfBatch) {

                writeBytes();

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            System.out.println(commandBase.getRawMessage());
        }
    }

    private void writeBytes() throws Exception {

        buffer.flip();
        raf.getChannel().write(buffer);
        buffer.clear();

        if ((raf.length() / ONE_MEGA_BYTE) > 500) {
            raf.close();
            createNewJournal();
        }
    }

    public void close() throws Exception {
        raf.close();
    }
}

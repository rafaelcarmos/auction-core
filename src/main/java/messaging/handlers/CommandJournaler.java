package messaging.handlers;

import com.lmax.disruptor.EventHandler;
import messaging.CommandBase;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;

public class CommandJournaler implements EventHandler<CommandBase> {

    private final int BUFFER_SIZE = 1024 * 10;

    private RandomAccessFile raf;
    private ByteBuffer buffer;

    public CommandJournaler() throws FileNotFoundException {

        buffer = ByteBuffer.allocate(BUFFER_SIZE);
        raf = new RandomAccessFile("randomAccessFileTest" + LocalDateTime.now().toString(), "rw");
    }

    @Override
    public void onEvent(CommandBase commandBase, long sequence, boolean endOfBatch) {
        try {

            byte[] msg = commandBase.getRawMessage().getBytes();

            if (msg.length >= buffer.remaining())
                writeBytes();

            buffer.put(msg);

            if (endOfBatch)
                writeBytes();

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(commandBase.getRawMessage());
        }
    }

    private void writeBytes() throws Exception {
        raf.write(buffer.array());
        buffer.clear();
    }

    public void close() throws Exception {
        raf.close();
    }
}

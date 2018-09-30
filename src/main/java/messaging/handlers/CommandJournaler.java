package messaging.handlers;

import com.lmax.disruptor.EventHandler;
import messaging.dispatchers.CommandBase;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommandJournaler implements EventHandler<CommandBase> {

    private final Path JOURNAL_DIRECTORY = Paths.get("/home/rafael/AuctionCoreCommandJournal/");
    private final int ONE_MEGA_BYTE = (1024 * 1024);
    private final int BLOCK_SIZE = 4096;
    private final int BUFFER_SIZE = BLOCK_SIZE * 8;
    private final byte[] lineSeparator = System.lineSeparator().getBytes(StandardCharsets.UTF_8);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private RandomAccessFile raf;
    private final ByteBuffer buffer;

    public CommandJournaler() throws Exception {

        if (Files.notExists(JOURNAL_DIRECTORY))
            Files.createDirectory(JOURNAL_DIRECTORY);

        buffer = ByteBuffer.allocate(BUFFER_SIZE);
        createNewJournal();
    }

    private void createNewJournal() throws Exception {

        String path = Paths.get(JOURNAL_DIRECTORY.toString(), "commandJournal_" + LocalDateTime.now().format(formatter)).toString();
        raf = new RandomAccessFile(path, "rw");

    }

    @Override
    public void onEvent(CommandBase commandBase, long sequence, boolean endOfBatch) {
        try {

            if ((commandBase.getRawMessage().length + lineSeparator.length) >= buffer.remaining())
                writeBytes();

            buffer.put(commandBase.getRawMessage());
            buffer.put(lineSeparator);

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

        if ((raf.length() / ONE_MEGA_BYTE) > 1000) {
            raf.close();
            createNewJournal();
        }
    }

    public void close() throws Exception {
        writeBytes();
        raf.close();
    }
}

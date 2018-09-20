package messaging.handlers;

import com.lmax.disruptor.EventHandler;
import messaging.dispatchers.CommandBase;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommandJournaler implements EventHandler<CommandBase> {

    private final Path JOURNAL_DIRECTORY = Paths.get("F:/AuctionCoreCommandJournal/");
    private final int ONE_MEGA_BYTE = (1024 * 1024);
    private final int BUFFER_SIZE = ONE_MEGA_BYTE * 4;
    private final String lineSeparator = System.lineSeparator();
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

            byte[] bytes = (commandBase.getRawMessage() + lineSeparator).getBytes();

            if ((bytes.length) >= buffer.remaining())
                writeBytes();

            buffer.put(bytes);

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

        if ((raf.length() / ONE_MEGA_BYTE) > 100) {
            raf.close();
            createNewJournal();
        }
    }

    public void close() throws Exception {
        writeBytes();
        raf.close();
    }
}

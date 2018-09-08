package messaging.handlers;

import com.lmax.disruptor.EventHandler;
import domain.auction.AuctionService;
import domain.auction.commands.Command;
import domain.auction.repository.Repository;
import messaging.CommandBase;

public class CommandParser implements EventHandler<CommandBase> {

    private final Repository repository;
    private final AuctionService auctionService;

    public CommandParser(Repository repository, AuctionService auctionService) {
        this.repository = repository;
        this.auctionService = auctionService;
    }

    @Override
    public void onEvent(CommandBase commandBase, long sequence, boolean endOfBatch) {
        try {

            commandBase.setSequence(sequence);
            String csv = commandBase.getRawMessage();

            Command command = Command.fromCSV(csv, sequence);

            commandBase.setCommand(command);
            auctionService.processCommand(command);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(commandBase.getRawMessage());
        }
    }
}

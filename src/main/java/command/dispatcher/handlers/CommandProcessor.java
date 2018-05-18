package command.dispatcher.handlers;

import com.lmax.disruptor.EventHandler;
import command.dispatcher.CommandBase;
import command.repository.Repository;
import command.service.AuctionService;

public class CommandProcessor implements EventHandler<CommandBase> {

    private final Repository repository;
    private final AuctionService auctionService;

    public CommandProcessor(Repository repository, AuctionService auctionService) {
        this.repository = repository;
        this.auctionService = auctionService;
    }

    @Override
    public void onEvent(CommandBase commandBase, long l, boolean b) {
        try {
            auctionService.processCommand(commandBase.getCommand());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

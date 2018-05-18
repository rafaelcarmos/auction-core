package command.dispatcher.handlers;

import com.lmax.disruptor.EventHandler;
import command.dispatcher.CommandBase;
import command.repository.Repository;
import command.service.AuctionService;

public class CommandProcessor implements EventHandler<CommandBase> {

    private final Repository repository;
    private final AuctionService controller;

    public CommandProcessor(Repository repository, AuctionService controller) {
        this.repository = repository;
        this.controller = controller;
    }

    @Override
    public void onEvent(CommandBase commandBase, long l, boolean b) {
        try {
            controller.processCommand(commandBase.getCommand());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

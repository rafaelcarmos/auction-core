package command.commands;

import java.time.LocalDateTime;
import java.util.UUID;

public class StartAuction extends Command {

    public StartAuction(UUID auctionId, LocalDateTime timestamp) {
        super(auctionId, timestamp);
    }
}

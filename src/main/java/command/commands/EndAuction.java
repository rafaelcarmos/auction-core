package command.commands;

import java.time.LocalDateTime;
import java.util.UUID;

public class EndAuction extends Command {

    public EndAuction(UUID auctionId, LocalDateTime timestamp) {
        super(auctionId, timestamp);
    }
}

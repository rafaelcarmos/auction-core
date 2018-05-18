package command.commands;

import java.time.LocalDateTime;
import java.util.UUID;

public class CancelAuction extends Command {

    public CancelAuction(UUID auctionId, LocalDateTime timestamp) {
        super(auctionId, timestamp);
    }
}

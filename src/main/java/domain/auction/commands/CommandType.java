package domain.auction.commands;

public enum CommandType {

    CREATE_AUCTION,
    CANCEL_AUCTION,
    START_AUCTION,
    FINISH_AUCTION,
    PLACE_BID;

    private static final CommandType[] values = CommandType.values();

    public static CommandType fromInt(int i) {
        return CommandType.values[i];
    }
}

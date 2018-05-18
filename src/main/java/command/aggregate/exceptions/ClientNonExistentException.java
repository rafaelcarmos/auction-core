package command.aggregate.exceptions;

public class ClientNonExistentException extends Exception {
    public ClientNonExistentException(String message) {
        super(message);
    }
}

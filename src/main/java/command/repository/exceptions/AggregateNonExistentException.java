package command.repository.exceptions;

public class AggregateNonExistentException extends Exception {
    public AggregateNonExistentException(String message) {
        super(message);
    }
}

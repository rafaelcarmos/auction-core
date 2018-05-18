package command.repository.exceptions;

public class AggregateNonExistantException extends Exception {
    public AggregateNonExistantException(String message) {
        super(message);
    }
}

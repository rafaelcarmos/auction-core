package domain.auction.exceptions;

public class ClientNonExistentException extends Exception {
    public ClientNonExistentException(String message) {
        super(message);
    }
}

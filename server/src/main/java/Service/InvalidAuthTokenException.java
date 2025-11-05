package Service;

public class InvalidAuthTokenException extends RuntimeException {
    public InvalidAuthTokenException(String message) {
        super(message);
    }
    public InvalidAuthTokenException(String message, Throwable ex) {
        super(message, ex);
    }
}

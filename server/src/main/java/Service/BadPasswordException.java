package Service;

public class BadPasswordException extends RuntimeException {
    public BadPasswordException(String message) {
        super(message);
    }
    public BadPasswordException(String message, Throwable ex) {
        super(message, ex);
    }
}

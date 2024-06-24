package app.prod.exception;

public class entityInitializationException extends Exception{
    public entityInitializationException() {
    }

    public entityInitializationException(String message) {
        super(message);
    }

    public entityInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public entityInitializationException(Throwable cause) {
        super(cause);
    }
}

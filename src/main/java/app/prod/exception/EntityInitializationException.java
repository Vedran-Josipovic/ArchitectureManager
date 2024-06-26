package app.prod.exception;

public class EntityInitializationException extends Exception{
    public EntityInitializationException() {
    }

    public EntityInitializationException(String message) {
        super(message);
    }

    public EntityInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityInitializationException(Throwable cause) {
        super(cause);
    }
}

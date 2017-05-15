package server.app.exeption;

public class NoArgumentException extends Exception {

    public NoArgumentException() {
    }

    public NoArgumentException(String message) {
        super(message);
    }

    public NoArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoArgumentException(Throwable cause) {
        super(cause);
    }
}

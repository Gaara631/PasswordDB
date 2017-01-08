package Exceptions;

/**
 * Created by gaara on 12/27/16.
 */
public class NullPasswordException extends Exception {
    public NullPasswordException() {
    }

    public NullPasswordException(String message) {
        super(message);
    }
}

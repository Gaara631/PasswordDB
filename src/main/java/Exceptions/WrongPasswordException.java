package Exceptions;

/**
 * Created by gaara on 12/27/16.
 */
public class WrongPasswordException extends Exception {
    public WrongPasswordException() {
        super();
    }

    public WrongPasswordException(String message) {
        super(message);
    }
}

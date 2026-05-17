package university.exceptions;

public class InsufficientHIndexException extends RuntimeException {
    public InsufficientHIndexException(int required, int actual) {
        super("Supervisor h-index too low: required >= " + required + ", but got " + actual);
    }
}

package university.exceptions;

public class MaxFailAttemptsException extends RuntimeException {
    public MaxFailAttemptsException(int maxFails) {
        super("Student has exceeded the maximum number of course failures (" + maxFails + ").");
    }
}

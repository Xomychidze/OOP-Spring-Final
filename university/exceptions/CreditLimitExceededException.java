package university.exceptions;

public class CreditLimitExceededException extends RuntimeException {
    public CreditLimitExceededException(int limit, int attempted) {
        super("Credit limit exceeded: max " + limit + " credits, attempted to reach " + attempted);
    }
}

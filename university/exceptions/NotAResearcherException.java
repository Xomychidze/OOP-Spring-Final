package university.exceptions;

public class NotAResearcherException extends RuntimeException {
    public NotAResearcherException(String userName) {
        super("User '" + userName + "' is not a Researcher and cannot join a ResearchProject.");
    }
}

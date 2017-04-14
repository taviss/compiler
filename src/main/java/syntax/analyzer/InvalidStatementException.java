package syntax.analyzer;

/**
 * Created by octav on 4/12/2017.
 */
public class InvalidStatementException extends RuntimeException {
    public InvalidStatementException(String message, int line) {
        super(message + " @ line " + line);
    }

}

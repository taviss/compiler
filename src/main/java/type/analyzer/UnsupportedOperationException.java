package type.analyzer;

/**
 * Created by octav on 5/3/2017.
 */
public class UnsupportedOperationException extends RuntimeException {

    public UnsupportedOperationException(String message, int line) {
        super(message + " @ line " + line);
    }
}

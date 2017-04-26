package domain.analyzer;

/**
 * Created by octav on 4/26/2017.
 */
public class SymbolRedefinitionException extends RuntimeException {
    public SymbolRedefinitionException(String message, int line) {
        super(message + " @ line " + line);
    }
}

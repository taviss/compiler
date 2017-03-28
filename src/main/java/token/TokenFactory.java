package token;

/**
 * Created by octav on 3/15/2017.
 *
 * A factory for creating {@link Token}
 */
public class TokenFactory {

    public Token createToken(TokenType type, int line, String value) {
        return new TextToken(type, line, value);
    }

    public Token createToken(TokenType type, int line, long value) {
        return new LongToken(type, line, value);
    }

    public Token createToken(TokenType type, int line, double value) {
        return new DoubleToken(type, line, value);
    }
}

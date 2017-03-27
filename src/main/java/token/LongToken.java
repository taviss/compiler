package token;

/**
 * Created by octav on 3/15/2017.
 */
public class LongToken extends Token {
    private long value;

    public LongToken(TokenType type, int line, long value) {
        super(type, line);
        this.value = value;
    }

    @Override
    public String getRawValue() {
        return String.valueOf(value);
    }
}

package token;

/**
 * Created by octav on 3/15/2017.
 */

/**
 * {@inheritDoc}
 */
public class DoubleToken extends Token {
    private double value;

    public DoubleToken(TokenType type, int line, double value) {
        super(type, line);
        this.value = value;
    }

    @Override
    public String getRawValue() {
        return String.valueOf(value);
    }
}

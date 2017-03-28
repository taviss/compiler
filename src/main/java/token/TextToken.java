package token;

/**
 * Created by octav on 3/15/2017.
 */
/**
 * {@inheritDoc}
 */
public class TextToken extends Token {
    private String value;

    public TextToken(TokenType code, int line, String value) {
        super(code, line);
        this.value = value;
    }

    @Override
    public String getRawValue() {
        return value;
    }
}

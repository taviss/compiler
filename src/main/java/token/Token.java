package token;

/**
 * Created by octav on 3/15/2017.
 */
public abstract class Token {
    private TokenType code;
    private int line;

    public Token(TokenType code, int line) {
        this.code = code;
        this.line = line;
    }

    public abstract String getRawValue();

    public TokenType getCode() {
        return code;
    }

    public void setCode(TokenType code) {
        this.code = code;
    }
}

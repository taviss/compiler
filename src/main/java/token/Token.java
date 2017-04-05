package token;

/**
 * Created by octav on 3/15/2017.
 *
 * Base class for a token. Contains information about the location where it was found.
 * This is split into 3 categories: {@link DoubleToken}, {@link LongToken}, {@link TextToken}
 * The difference in each of them is the type of the contained value
 */
public abstract class Token {
    /**
     * The type of the token, see {@link definition.Definitions}
     */
    private TokenType code;

    /**
     * The line in the parsed file
     */
    private int line;

    /**
     * The start index of the match
     */
    private int startMatchIndex;

    /**
     * The end index of the match
     */
    private int endMatchIndex;

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

    public int getStartMatchIndex() {
        return startMatchIndex;
    }

    public void setStartMatchIndex(int startMatchIndex) {
        this.startMatchIndex = startMatchIndex;
    }

    public int getEndMatchIndex() {
        return endMatchIndex;
    }

    public void setEndMatchIndex(int endMatchIndex) {
        this.endMatchIndex = endMatchIndex;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String toString() {
        return "[Value=" + getRawValue() + ", Type=" + getCode() + ", Line=" + getLine() + ", StartIndex=" + startMatchIndex + "]\n";
    }

    public boolean equals(Object other) {
        if(other instanceof Token) {
            Token otherToken = (Token) other;
            return otherToken.getLine() == this.getLine() && otherToken.getStartMatchIndex() == this.getStartMatchIndex() && otherToken.getCode() == this.getCode();
        }
        return false;
    }
}

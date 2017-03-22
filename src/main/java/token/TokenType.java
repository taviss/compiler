package token;

/**
 * Created by octav on 3/15/2017.
 */
public enum TokenType {
    ID("ID"),
    END("END"),
    CT_INT("CT_INT"),
    CT_REAL("CT_REAL"),
    CT_CHAR("CT_CHAR"),
    CT_STRING("CT_STRING"),
    BREAK("BREAK"),
    CHAR("CHAR"),
    DOUBLE("DOUBLE"),
    ELSE("ELSE"),
    FOR("FOR"),
    IF("IF"),
    INT("INT"),
    RETURN("RETURN"),
    STRUCT("STRUCT"),
    VOID("VOID"),
    WHILE("WHILE"),
    COMMA("COMMA"),
    SEMICOLON("SEMICOLON"),
    LPAR("LPAR"),
    RPAR("RPAR"),
    LBRACKET("LBRACKET"),
    RBRACKET("RBRACKET"),
    LACC("LACC"),
    RACC("RACC"),
    ADD("ADD"),
    SUB("SUB"),
    MUL("MUL"),
    DIV("DIV"),
    DOT("DUT"),
    AND("AND"),
    OR("OR"),
    NOT("NOT"),
    ASSIGN("ASSIGN"),
    EQUAL("EQUAL"),
    NOTEQ("NOTEQ"),
    LESS("LESS"),
    LESSEQ("LESSEQ"),
    GREATER("GREATER"),
    GREATEREQ("GREATEREQ");

    private final String text;

    /**
     * @param text
     */
    private TokenType(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
}

package domain.symbols;

public enum TypeBase {
    TB_INT("INT"),
    TB_DOUBLE("DOUBLE"),
    TB_CHAR("CHAR"),
    TB_STRUCT("STRUCT"),
    TB_VOID("VOID");

    private final String text;

    /**
     * @param text
     */
    private TypeBase(final String text) {
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

package domain.symbols;

public class StructType extends Type{
    private Symbol symbol;
    
    public StructType(StructType anotherStructType) {
        super.setTypeBase(anotherStructType.getTypeBase());
        super.setNoOfElements(anotherStructType.getNoOfElements());
        this.symbol = anotherStructType.getSymbol();
    }

    public StructType() {
        super.setTypeBase(TypeBase.TB_STRUCT);
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }
}

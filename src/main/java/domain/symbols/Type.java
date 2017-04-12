package domain.symbols;

public class Type {
    private TypeBase typeBase;

    public Type() {

    }

    public Type(TypeBase typeBase) {
        this.typeBase = typeBase;
    }

    public TypeBase getTypeBase() {
        return typeBase;
    }

    public void setTypeBase(TypeBase typeBase) {
        this.typeBase = typeBase;
    }
}

package domain.symbols;

public class Type {
    private TypeBase typeBase;
    private int noOfElements;

    public Type() {

    }

    public Type(TypeBase typeBase) {
        this.typeBase = typeBase;
    }

    public Type(TypeBase typeBase, int noOfElements) {
        this.typeBase = typeBase;
        this.noOfElements = noOfElements;
    }

    public TypeBase getTypeBase() {
        return typeBase;
    }

    public void setTypeBase(TypeBase typeBase) {
        this.typeBase = typeBase;
    }

    public int getNoOfElements() {
        return noOfElements;
    }

    public void setNoOfElements(int noOfElements) {
        this.noOfElements = noOfElements;
    }

    public String toString() {
        return "[TypeBase=" + getTypeBase() + ", elements=" + getNoOfElements() + "]";
    }
}

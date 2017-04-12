package domain.symbols;

public class Symbol {
    private String name;
    private TypeBase typeBase;
    private ClassType cls;
    private int depth;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClassType getCls() {
        return cls;
    }

    public void setCls(ClassType cls) {
        this.cls = cls;
    }

    public TypeBase getTypeBase() {
        return typeBase;
    }

    public void setTypeBase(TypeBase typeBase) {
        this.typeBase = typeBase;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}

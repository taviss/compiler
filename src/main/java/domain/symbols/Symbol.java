package domain.symbols;

public class Symbol {
    private String name;
    private Type type;
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}

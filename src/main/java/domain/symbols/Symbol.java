package domain.symbols;

import runtime.instructions.ExtFunc;

public class Symbol {
    private String name;
    private Type type;
    private ClassType cls;
    private int depth;
    private MemType memType;
    private ExtFunc extFunc;
    private int offset;

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

    public MemType getMemType() {
        return memType;
    }

    public void setMemType(MemType memType) {
        this.memType = memType;
    }

    public String toString() {
       return "\n" + getName() + ":\n\ttype=" + getType() + "\n\tcls=" + getCls() + "\n\tmemType=" + getMemType() + "\n\tdepth=" + getDepth();
    }

    public ExtFunc getExtFunc() {
        return extFunc;
    }

    public void setExtFunc(ExtFunc extFunc) {
        this.extFunc = extFunc;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}

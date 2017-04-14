package domain.symbols;

import java.util.ArrayList;
import java.util.List;

public class FuncSymbol extends Symbol {
    private List<Symbol> args;

    public FuncSymbol() {
        this.args = new ArrayList<>();
    }

    public List<Symbol> getArgs() {
        return args;
    }

    public void setArgs(List<Symbol> args) {
        this.args = args;
    }

    public String toString() {
        String string = "\n" + getName() + ":\n\ttype=" + getType() + "\n\tcls=" + getCls() + "\n\tmemType=" + getMemType() + "\n\tdepth=" + getDepth() + "\n\targs=[";
        StringBuilder stringBuilder = new StringBuilder(string);
        for(Symbol arg : args) {
           stringBuilder.append("\n\t\tname=" + arg.getName() + ",\n\t\ttype=" + arg.getType() + ",\n\t\tcls=" + arg.getCls() + ",\n\t\tmemType=" + arg.getMemType() + ",\n\t\tdepth=" + arg.getDepth());
        }
        stringBuilder.append("\n\t]");
        return stringBuilder.toString();
    }
}

package domain.symbols;

import java.util.List;

public class FuncSymbol extends Symbol {
    private List<Symbol> args;

    public List<Symbol> getArgs() {
        return args;
    }

    public void setArgs(List<Symbol> args) {
        this.args = args;
    }
}

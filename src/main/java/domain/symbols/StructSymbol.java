package domain.symbols;

import java.util.ArrayList;
import java.util.List;

public class StructSymbol extends Symbol {
    private List<Symbol> members;

    public StructSymbol() {
        this.members = new ArrayList<>();
    }

    public List<Symbol> getMembers() {
        return members;
    }

    public void setMembers(List<Symbol> members) {
        this.members = members;
    }
}

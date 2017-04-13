package domain.analyzer;

import domain.symbols.*;
import token.Token;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static domain.symbols.ClassType.CLS_EXTFUNC;
import static domain.symbols.ClassType.CLS_VAR;

public class DomainAnalyzer {
    private List<Symbol> symbolList;
    private int currentDepth;
    private StructSymbol currentStruct;
    private FuncSymbol currentFunc;

    public DomainAnalyzer() {
        this.symbolList = new ArrayList<>();
    }

    public Symbol addSymbol(String name, ClassType classType) {
        Symbol symbol = null;
        switch(classType) {
            case CLS_VAR: {
                symbol = new Symbol();
                symbol.setName(name);
                symbol.setCls(classType);
                symbol.setDepth(getCurrentDepth());
                symbolList.add(symbol);
                break;
            }
            case CLS_FUNC:
            case CLS_EXTFUNC: {
                symbol = new FuncSymbol();
                symbol.setName(name);
                symbol.setCls(classType);
                symbol.setDepth(getCurrentDepth());
                symbolList.add(symbol);
                break;
            }
            case CLS_STRUCT: {
                symbol = new StructSymbol();
                symbol.setName(name);
                symbol.setCls(classType);
                symbol.setDepth(getCurrentDepth());
                symbolList.add(symbol);
                break;
            }
        }

        return symbol;
    }

    public Symbol addSymbol(List<Symbol> symbols, String name, ClassType classType) {
        Symbol symbol = new Symbol();
        symbol.setName(name);
        symbol.setCls(classType);
        symbol.setDepth(getCurrentDepth());
        symbols.add(symbol);
        return symbol;
    }

    public Symbol findSymbol(String name) {
        if(symbolList.isEmpty()) return null;
        ListIterator<Symbol> iterator = symbolList.listIterator(symbolList.size());
        while(iterator.hasPrevious()) {
            Symbol symbol = iterator.previous();
            if(symbol.getName().equals(name)) return symbol;
        }
        return null;
    }

    public Symbol findSymbol(List<Symbol> symbols, String name) {
        if(symbols.isEmpty()) return null;
        ListIterator<Symbol> iterator = symbols.listIterator(symbols.size());
        while(iterator.hasPrevious()) {
            Symbol symbol = iterator.previous();
            if(symbol.getName().equals(name)) return symbol;
        }
        return null;
    }

    public List<Symbol> getSymbolList() {
        return this.symbolList;
    }

    public void addVar(Token token, Type type) {
        if(getCurrentStruct() != null) {
            if(findSymbol(currentStruct.getMembers(), token.getRawValue()) != null) {
                //Error
            } else {
                Symbol symbol = addSymbol(currentStruct.getMembers(), token.getRawValue(), CLS_VAR);
                symbol.setType(type);
            }
        } else if(getCurrentFunc() != null) {
            Symbol symbol = findSymbol(token.getRawValue());
            if(symbol != null && symbol.getDepth() == getCurrentDepth()) {
                //Error
            }
            symbol = addSymbol(token.getRawValue(),CLS_VAR);
            symbol.setType(type);
            symbol.setMemType(MemType.MEM_LOCAL);
        } else {
            if(findSymbol(token.getRawValue()) != null) {
                //Error
            }
            Symbol symbol = addSymbol(token.getRawValue(),CLS_VAR);
            symbol.setType(type);
            symbol.setMemType(MemType.MEM_GLOBAL);
        }
    }

    public void deleteSymbolsAfter(Symbol symbol) {
        ListIterator<Symbol> iterator = symbolList.listIterator(symbolList.indexOf(symbol) + 1);
        while(iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    public void increaseCurrentDepth() {
        this.currentDepth++;
    }

    public void decreaseCurrentDepth() {
        this.currentDepth--;
    }

    public int getCurrentDepth() {
        return currentDepth;
    }

    public void setCurrentDepth(int currentDepth) {
        this.currentDepth = currentDepth;
    }

    public StructSymbol getCurrentStruct() {
        return currentStruct;
    }

    public void setCurrentStruct(StructSymbol currentStruct) {
        this.currentStruct = currentStruct;
    }

    public FuncSymbol getCurrentFunc() {
        return currentFunc;
    }

    public void setCurrentFunc(FuncSymbol currentFunc) {
        this.currentFunc = currentFunc;
    }
}

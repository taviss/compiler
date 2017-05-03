package utils;

import domain.symbols.StructType;
import domain.symbols.Type;
import domain.symbols.TypeBase;
import syntax.analyzer.InvalidStatementException;
import syntax.analyzer.SyntaxAnalyzer;
import type.analyzer.UnsupportedOperationException;

import static domain.symbols.TypeBase.TB_DOUBLE;
import static domain.symbols.TypeBase.TB_INT;
import static domain.symbols.TypeBase.TB_STRUCT;

/**
 * Created by octav on 3/29/2017.
 */
public class Utils {
    public static String replaceCharAt(String s, int pos, char c) {
        return s.substring(0,pos) + c + s.substring(pos+1);
    }

    public static String fixEscapedChars(String s) {
        char[] charArray = s.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < charArray.length; i++) {
            if((charArray[i] == 'n' || charArray[i] == 't' || charArray[i] == '\"' || charArray[i] == '\\') && i > 0 && charArray[i-1] == '\\') {
                switch(charArray[i]) {
                    case 'n': stringBuilder.append('\n'); break;
                    case 't': stringBuilder.append('\t'); break;
                    case '\"': stringBuilder.append('\"'); break;
                    case '\\': stringBuilder.append('\\'); break;
                }
            } else {
                if(charArray[i] == '\\') continue;

                stringBuilder.append(charArray[i]);
            }
        }

        return stringBuilder.toString();
    }

    public static void cast(Type dest, Type src) {
        if(src.getNoOfElements() > -1) {
            if(dest.getNoOfElements() > -1) {
                if(src.getTypeBase() != dest.getTypeBase()) {
                    throw new InvalidStatementException("An array cannot be converted to another type of array", SyntaxAnalyzer.getInstance().getCurrentToken().getLine());
                }
            } else {
                throw new InvalidStatementException("An array cannot be converted to a non array", SyntaxAnalyzer.getInstance().getCurrentToken().getLine());
            }
        } else {
            if(dest.getNoOfElements() > -1) {
                throw new InvalidStatementException("A non array cannot be converted to an array", SyntaxAnalyzer.getInstance().getCurrentToken().getLine());
            }
        }

        switch(src.getTypeBase()) {
            case TB_CHAR:
            case TB_DOUBLE:
            case TB_INT: {
                switch (dest.getTypeBase()) {
                    case TB_CHAR:
                    case TB_DOUBLE:
                    case TB_INT:
                        return;
                }
            }
            case TB_STRUCT:
                if(dest.getTypeBase() == TB_STRUCT){
                    if(((StructType) src).getSymbol() != ((StructType) dest).getSymbol())
                        throw new InvalidStatementException("A structure cannot be converted to another one", SyntaxAnalyzer.getInstance().getCurrentToken().getLine());
                    return;
                }
        }
        throw new InvalidStatementException("Incompatible types", SyntaxAnalyzer.getInstance().getCurrentToken().getLine());
    }

    public static Type getArithType(Type type1, Type type2) {
        if(type1.getTypeBase() == type2.getTypeBase()) return type1;

        TypeBase resultedTypeBase = type1.getTypeBase();
        switch(type1.getTypeBase()) {
            case TB_CHAR: {
                switch (type2.getTypeBase()) {
                    case TB_INT: {
                        resultedTypeBase = TB_INT;
                        break;
                    }
                    default:
                        throw new UnsupportedOperationException("Could not get resulted type for " + type1.getTypeBase() + " and " + type2.getTypeBase(), SyntaxAnalyzer.getInstance().getCurrentToken().getLine());
                }
                break;
            }
            case TB_INT: {
                switch (type2.getTypeBase()) {
                    case TB_CHAR: return getArithType(type2, type1);
                    case TB_INT: break;
                    case TB_DOUBLE: resultedTypeBase = TB_DOUBLE;
                    default:
                        throw new UnsupportedOperationException("Could not get resulted type for " + type1.getTypeBase() + " and " + type2.getTypeBase(), SyntaxAnalyzer.getInstance().getCurrentToken().getLine());
                }
                break;
            }
            case TB_DOUBLE: {
                switch (type2.getTypeBase()) {
                    case TB_INT: return getArithType(type2, type1);
                    case TB_DOUBLE: break;
                    default:
                        throw new UnsupportedOperationException("Could not get resulted type for " + type1.getTypeBase() + " and " + type2.getTypeBase(), SyntaxAnalyzer.getInstance().getCurrentToken().getLine());
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Could not get resulted type for " + type1.getTypeBase() + " and " + type2.getTypeBase(), SyntaxAnalyzer.getInstance().getCurrentToken().getLine());
        }
        return new Type(resultedTypeBase, type1.getNoOfElements());
    }
}

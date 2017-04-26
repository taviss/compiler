package syntax.analyzer;

import domain.analyzer.DomainAnalyzer;
import domain.symbols.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import token.Token;
import token.TokenType;
import type.analyzer.ReturnValue;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static domain.symbols.ClassType.CLS_FUNC;
import static domain.symbols.ClassType.CLS_VAR;
import static token.TokenType.*;

/**
 * Created by octav on 3/29/2017.
 *
 * Class that takes the resulted tokens from the {@link token.analyzer.TokenAnalyzer}
 * and verifies the syntax
 */
public class SyntaxAnalyzer {
    private static final Logger LOG = LoggerFactory.getLogger(SyntaxAnalyzer.class);

    private List<Token> tokens;
    private Iterator<Token> tokenIterator;
    private Token token;
    private DomainAnalyzer domainAnalyzer;

    public SyntaxAnalyzer(List<Token> tokens) {
        this.tokens = tokens;
        this.tokenIterator = tokens.iterator();
        this.domainAnalyzer = new DomainAnalyzer();
        getNext();
    }

    private boolean getNext() {
        if(tokenIterator.hasNext()) {
            token = tokenIterator.next();
            return true;
        }
        return false;
    }

    private void goBackTo(Token token) {
        tokenIterator = tokens.iterator();
        Token currentToken = tokenIterator.next();
        while(!currentToken.equals(token)) {
            currentToken = tokenIterator.next();
        }
        this.token = currentToken;
    }

    public void start() {
        while(tokenIterator.hasNext()) {
            if(!unit()) {
                break;
            }
        }
        System.out.println(domainAnalyzer.getSymbolList().toString());
    }

    public boolean unit() {
        if(declStruct()) return true;
        if(declFunc()) return true;
        if(declVar()) return true;
        if(token.getCode() == END) {
            getNext();
            return  true;
        }
        return false;
    }

    public boolean declFunc() {
        Token currentToken = token;
        Type type = new Type();
        if((type = typeBase(type)) != null) {
            if(token.getCode() == MUL) {
                type.setNoOfElements(0);
                getNext();
            } else {
                type.setNoOfElements(-1);
            }

            if(token.getCode() == ID) {
                String tokenText = token.getRawValue();
                getNext();
                if(token.getCode() == LPAR) {
                    if(domainAnalyzer.findSymbol(tokenText) != null)
                        throw new InvalidStatementException("Symbol already defined " + tokenText, token.getLine());
                    domainAnalyzer.setCurrentFunc((FuncSymbol) domainAnalyzer.addSymbol(tokenText, CLS_FUNC));
                    FuncSymbol funcSymbol = domainAnalyzer.getCurrentFunc();
                    funcSymbol.setType(type);
                    domainAnalyzer.increaseCurrentDepth();
                    getNext();
                    if(funcArg()) {
                        while(token.getCode() == COMMA) {
                            getNext();
                            funcArg();
                        }
                    }
                    if(token.getCode() == RPAR) {
                        domainAnalyzer.decreaseCurrentDepth();
                        getNext();
                        if(stmCompound()) {
                            domainAnalyzer.deleteSymbolsAfter(domainAnalyzer.getCurrentFunc());
                            domainAnalyzer.setCurrentFunc(null);
                            return true;
                        } else throw new InvalidStatementException("Missing closing `}` or invalid statement", token.getLine());
                    } else throw new InvalidStatementException("Missing closing `)` or invalid statement", token.getLine());
                }
            }
        }

        type = new Type();
        if(token.getCode() == VOID) {
            type.setTypeBase(TypeBase.TB_VOID);
            getNext();
            if(token.getCode() == ID) {
                String tokenText = token.getRawValue();
                getNext();
                if (token.getCode() == LPAR) {
                    if(domainAnalyzer.findSymbol(tokenText) != null)
                        throw new InvalidStatementException("Symbol already defined " + tokenText, token.getLine());
                    domainAnalyzer.setCurrentFunc((FuncSymbol) domainAnalyzer.addSymbol(tokenText, CLS_FUNC));
                    FuncSymbol funcSymbol = domainAnalyzer.getCurrentFunc();
                    funcSymbol.setType(type);
                    domainAnalyzer.increaseCurrentDepth();
                    getNext();
                    if (funcArg()) {
                        while (token.getCode() == COMMA) {
                            getNext();
                            funcArg();
                        }
                    }
                    if (token.getCode() == RPAR) {
                        domainAnalyzer.decreaseCurrentDepth();
                        getNext();
                        if (stmCompound()) {
                            domainAnalyzer.deleteSymbolsAfter(domainAnalyzer.getCurrentFunc());
                            domainAnalyzer.setCurrentFunc(null);
                            return true;
                        } else throw new InvalidStatementException("Invalid statement", token.getLine());
                    } else throw new InvalidStatementException("Missing closing `)` or invalid statement", token.getLine());
                }
            }
        }
        goBackTo(currentToken);
        return false;
    }

    public boolean stmCompound() {
        Symbol start = domainAnalyzer.getSymbolList().get(domainAnalyzer.getSymbolList().size() - 1);
        if(token.getCode() == LACC) {
            domainAnalyzer.increaseCurrentDepth();
            getNext();
            while(declVar() || stm()) {

            }
            if(token.getCode() == RACC) {
                domainAnalyzer.decreaseCurrentDepth();
                domainAnalyzer.deleteSymbolsAfter(start);
                getNext();
                return true;
            }
        }
        return false;
    }

    public boolean stm() {
        if(stmCompound()) {
            return true;
        }
        switch(token.getCode()) {
            case IF: {
                getNext();
                if (token.getCode() == LPAR) {
                    getNext();
                    ReturnValue returnValue = expr();
                    if (returnValue != null) {
                        if(returnValue.getType().getTypeBase() == TypeBase.TB_STRUCT) throw new InvalidStatementException("A structure cannot be logically tested", token.getLine());

                        if (token.getCode() == RPAR) {
                            getNext();
                            if (stm()) {
                                if (token.getCode() == ELSE) {
                                    getNext();
                                    if (stm()) {
                                        return true;
                                    } else throw new InvalidStatementException("Missing statement", token.getLine());
                                } else return true;
                            } else throw new InvalidStatementException("Missing statement", token.getLine());
                        } else throw new InvalidStatementException("Missing closing `)`", token.getLine());
                    } else throw new InvalidStatementException("Missing expression in if", token.getLine());
                } else throw new InvalidStatementException("Missing opening `(`", token.getLine());
            }
            case WHILE: {
                getNext();
                if (token.getCode() == LPAR) {
                    getNext();
                    ReturnValue returnValue = expr();
                    if (returnValue != null) {
                        if(returnValue.getType().getTypeBase() == TypeBase.TB_STRUCT) throw new InvalidStatementException("A structure cannot be logically tested", token.getLine());

                        if (token.getCode() == RPAR) {
                            getNext();
                            if (stm()) {
                                return true;
                            } else throw new InvalidStatementException("Missing statement", token.getLine());
                        } else throw new InvalidStatementException("Missing closing `)`", token.getLine());
                    } else throw new InvalidStatementException("Missing expression in while", token.getLine());
                } else throw new InvalidStatementException("Missing opening `(`", token.getLine());
            }
            case FOR: {
                getNext();
                if(token.getCode() == LPAR) {
                    getNext();
                    expr();
                    if(token.getCode() == SEMICOLON) {
                        getNext();
                        ReturnValue returnValue = expr();
                        if (returnValue != null) {
                            if (returnValue.getType().getTypeBase() == TypeBase.TB_STRUCT)
                                throw new InvalidStatementException("A structure cannot be logically tested", token.getLine());
                        }
                        if(token.getCode() == SEMICOLON) {
                            getNext();
                            expr();
                            if(token.getCode() == RPAR) {
                                getNext();
                                if(stm()) {
                                    return true;
                                } else throw new InvalidStatementException("Missing statement", token.getLine());
                            } else throw new InvalidStatementException("Expected closing `)`", token.getLine());
                        } else throw new InvalidStatementException("Expected `;`", token.getLine());
                    } else throw new InvalidStatementException("Expected `;`", token.getLine());
                } else throw new InvalidStatementException("Expected opening `(`", token.getLine());
            }
            case BREAK: {
                getNext();
                if(token.getCode() == SEMICOLON) {
                    getNext();
                    return true;
                } else throw new InvalidStatementException("Missing `;`", token.getLine());
            }
            case RETURN: {
                getNext();
                ReturnValue returnValue = expr();
                if (returnValue != null) {
                    if (domainAnalyzer.getCurrentFunc().getType().getTypeBase() == TypeBase.TB_VOID)
                        throw new InvalidStatementException("Void function " + domainAnalyzer.getCurrentFunc().getName() + " cannot return a value", token.getLine());
                }
                //TODO Cast method ??????? returnValue is lost anyways????
                if(token.getCode() == SEMICOLON) {
                    getNext();
                    return true;
                } else throw new InvalidStatementException("Missing `;`", token.getLine());
            }
            default:
                break;

        }
        expr();
        if(token.getCode() == SEMICOLON) {
            getNext();
            return true;
        }
        return false;
    }

    public boolean funcArg() {
        Type type = new Type();
        if((type = typeBase(type)) != null) {
            if(token.getCode() == ID) {
                String tokenText = token.getRawValue();
                getNext();
                Type arrayDeclType = arrayDecl(type);
                if(arrayDeclType != null) {
                    type = arrayDeclType;
                } else {
                    type.setNoOfElements(-1);
                }
                Symbol symbol = domainAnalyzer.addSymbol(tokenText, CLS_VAR);
                symbol.setMemType(MemType.MEM_ARG);
                symbol.setType(type);
                symbol = domainAnalyzer.addSymbol(domainAnalyzer.getCurrentFunc().getArgs(), tokenText, CLS_VAR);
                symbol.setMemType(MemType.MEM_ARG);
                symbol.setType(type);
                return true;
            } else throw new InvalidStatementException("Missing identifier", token.getLine());
        }
        return false;
    }

    public boolean declStruct() {
        if(token.getCode() == STRUCT) {
            Token currentToken = token;
            getNext();
            if (token.getCode() == ID) {
                String tokenText = token.getRawValue();
                getNext();
                if (token.getCode() == LACC) {
                    if(domainAnalyzer.findSymbol(tokenText) != null) {
                        throw new InvalidStatementException("Symbol already defined " + tokenText, token.getLine());
                    }
                    domainAnalyzer.setCurrentStruct((StructSymbol) domainAnalyzer.addSymbol(tokenText, ClassType.CLS_STRUCT));
                    getNext();
                    while(declVar()) {
                    }
                    if(token.getCode() == RACC) {
                        getNext();
                        if(token.getCode() == SEMICOLON) {
                            getNext();
                            domainAnalyzer.setCurrentStruct(null);
                            return true;
                        } else {
                            throw new InvalidStatementException("Missing `;`", token.getLine());
                        }
                    } else {
                        throw new InvalidStatementException("Missing closing '}", token.getLine());
                    }
                }
            }
            goBackTo(currentToken);
        }
        return false;
    }

    public Type typeBase(Type type) {
        if(token.getCode() == INT || token.getCode() == DOUBLE || token.getCode() == CHAR) {
            TokenType tokenType = token.getCode();
            getNext();
            type.setTypeBase(TypeBase.valueOf("TB_" + tokenType.toString()));
            return type;
        }

        if(token.getCode() == STRUCT) {
            getNext();
            if(token.getCode() == ID) {
                Symbol symbol = domainAnalyzer.findSymbol(token.getRawValue());
                if(symbol == null) {
                    throw new InvalidStatementException("Undefined symbol `" + token.getRawValue() + "`", token.getLine());
                } else if(symbol.getCls() != ClassType.CLS_STRUCT) {
                    throw new InvalidStatementException(token.getRawValue() + " is not a struct", token.getLine());
                }
                getNext();
                StructType structType = new StructType();
                structType.setSymbol(symbol);
                structType.setNoOfElements(type.getNoOfElements());
                return structType;
            } else throw new InvalidStatementException("Missing identifier", token.getLine());
        }
        return null;
    }

    public boolean declVar() {
        Type type = new Type();
        if((type = typeBase(type)) != null) {
            if (token.getCode() == ID) {
                Token addToken = token;
                getNext();
                Type arrayDeclType;
                if((arrayDeclType = arrayDecl(type)) != null) {
                    type = arrayDeclType;
                } else {
                    type.setNoOfElements(-1);
                }
                domainAnalyzer.addVar(addToken, type);
                while (token.getCode() == COMMA) {
                    getNext();
                    if (token.getCode() == ID) {
                        addToken = token;
                        getNext();
                        if((arrayDeclType = arrayDecl(type)) != null) {
                            type = arrayDeclType;
                        } else {
                            type.setNoOfElements(-1);
                        }
                        domainAnalyzer.addVar(addToken, type);
                    } else {
                        throw new InvalidStatementException("Missing identifier", token.getLine());
                    }
                }
                if (token.getCode() == SEMICOLON) {
                    getNext();
                    return true;
                } else {
                    throw new InvalidStatementException("Missing `;`", token.getLine());
                }
            } else {
                throw new InvalidStatementException("Missing identifier", token.getLine());
            }
        }
        return false;
    }

    public Type arrayDecl(Type type) {
        if(token.getCode() == LBRACKET) {
            getNext();
            ReturnValue returnValue = expr();
            if(returnValue != null) {
                if(!returnValue.isCtVal()) throw new InvalidStatementException("Array size is not constant", token.getLine());
                if(returnValue.getType().getTypeBase() != TypeBase.TB_INT) throw new InvalidStatementException("Array size is not an integer", token.getLine());
                type.setNoOfElements((int)returnValue.getConstantValue());
            } else {
                type.setNoOfElements(0);
            }
            if(token.getCode() == RBRACKET) {
                getNext();
                return type;
            } else {
                throw new InvalidStatementException("Missing closing `]`", token.getLine());
            }
        }
        return null;
    }

    public ReturnValue expr() {
        return exprAssign();
    }

    public ReturnValue exprAssign() {
        Token currentToken = token;
        ReturnValue returnValue = exprUnary();
        if(returnValue != null) {
            if (token.getCode() == ASSIGN) {
                getNext();
                if (exprAssign()) {
                    return true;
                } else {
                    throw new InvalidStatementException("Missing assign expression", token.getLine());
                }
            }
            //Go back since there's nothing else in here
            goBackTo(currentToken);
        }
        goBackTo(currentToken);
        if (exprOr()) {
            return true;
        }
        goBackTo(currentToken);
        return false;
    }

    public boolean exprOr() {
        if(exprAnd()) {
            return exprOr1();
        }
        return false;
    }

    public boolean exprOr1() {
        if(token.getCode() == OR) {
            getNext();
            if(exprAnd()) {
                exprOr1();
            } else {
                throw new InvalidStatementException("Missing statement", token.getLine());
            }
        }
        return true;
    }

    public boolean exprAnd() {
        if(exprEq()) {
            return exprAnd1();
        }
        return false;
    }

    public boolean exprEq() {
        if(exprRel()) {
            return exprEq1();
        }
        return false;
    }

    public boolean exprRel() {
        if(exprAdd()) {
            return exprRel1();
        }
        return false;
    }

    public boolean exprAdd() {
        if(exprMul()) {
            return exprAdd1();
        }
        return false;
    }

    public boolean exprMul() {
        if(exprCast()) {
            return exprMul1();
        }
        return false;
    }

    public boolean exprMul1() {
        if(token.getCode() == MUL || token.getCode() == DIV) {
            getNext();
            if(exprCast()) {
                exprMul1();
            } else {
                throw new InvalidStatementException("Missing expression ", token.getLine());
            }
        }
        return true;
    }

    public boolean exprCast() {
        Token currentToken = token;
        if(token.getCode() == LPAR) {
            getNext();
            if(typeName(new Type()) != null) {
                if(token.getCode() == RPAR) {
                    getNext();
                    if(exprCast()) {
                        return true;
                    }
                }
            }
            goBackTo(currentToken);
        }

        return exprUnary();
    }

    public Type typeName(Type type) {
        if((type = typeBase(type)) != null) {
            Type arrayDeclType;
            if((arrayDeclType = arrayDecl(type)) != null) {
                type = arrayDeclType;
            } else {
                type.setNoOfElements(-1);
            }
            return type;
        }
        return null;
    }

    public boolean exprAdd1() {
        if(token.getCode() == ADD || token.getCode() == SUB) {
            getNext();
            if(exprMul()) {
                exprAdd1();
            } else {
                throw new InvalidStatementException("Missing expression after (+/-)", token.getLine());
            }
        }
        return true;
    }

    public boolean exprRel1() {
        if(token.getCode() == LESS || token.getCode() == LESSEQ || token.getCode() == GREATER
                || token.getCode() == GREATEREQ) {
            getNext();
            if(exprAdd()) {
                exprRel1();
            } else {
                throw new InvalidStatementException("Missing expression", token.getLine());
            }
        }
        return true;
    }

    public boolean exprEq1() {
        if(token.getCode() == EQUAL || token.getCode() == NOTEQ) {
            getNext();
            if(exprRel()) {
                exprEq1();
            } else {
                throw new InvalidStatementException("Missing expression", token.getLine());
            }
        }
        return true;
    }

    public boolean exprAnd1() {
        if(token.getCode() == AND) {
            getNext();
            if(exprEq()) {
                exprAnd1();
            } else {
                throw new InvalidStatementException("Missing expression", token.getLine());
            }
        }
        return true;
    }

    public boolean exprUnary() {
        Token currentToken = token;
        if(token.getCode() == SUB || token.getCode() == NOT) {
            getNext();
            if(exprUnary()) {
                return true;
            }
            goBackTo(currentToken);
        }


        return exprPostfix();
    }

    public boolean exprUnary1() {
        if(exprPostfix()) {
            exprUnary1();
        }
        return true;
    }

    public boolean exprPostfix() {
        if(exprPrimary()) {
            return exprPostfix1();
        }
        return false;
    }

    public boolean exprPostfix1() {
        if(token.getCode() == LBRACKET) {
            getNext();
            if(expr()) {
                if(token.getCode() == RBRACKET) {
                    getNext();
                    return exprPostfix1();
                } else {
                    throw new InvalidStatementException("Missing expression", token.getLine());
                }
            } else {
                throw new InvalidStatementException("Missing expression", token.getLine());
            }
        }
        else if(token.getCode() == DOT) {
            getNext();
            if(token.getCode() == ID) {
                getNext();
                exprPostfix1();
            } else {
                throw new InvalidStatementException("Missing ID", token.getLine());
            }
        }
        return true;
    }

    public boolean exprPrimary() {
        if(token.getCode() == ID) {
            if(domainAnalyzer.findSymbol(token.getRawValue()) == null) throw new InvalidStatementException("Undefined symbol `" + token.getRawValue() + "`", token.getLine());
            getNext();
            if(token.getCode() == LPAR) {
                getNext();
                expr();
                while(token.getCode() == COMMA) {
                    getNext();
                    expr();
                }
                if(token.getCode() == RPAR) {
                    getNext();
                    return true;
                } else {
                    throw new InvalidStatementException("Missing expression", token.getLine());
                }
            }
            return true;
        } else if(token.getCode() == CT_CHAR || token.getCode() == CT_INT || token.getCode() == CT_REAL
                || token.getCode() == CT_STRING) {
            getNext();
            return true;
        } else if(token.getCode() == LPAR) {
            getNext();
            if(expr()) {
                if(token.getCode() == RPAR) {
                    getNext();
                    return true;
                }
            }
        }
        return false;
    }
}

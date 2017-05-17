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

import static domain.symbols.ClassType.CLS_EXTFUNC;
import static domain.symbols.ClassType.CLS_FUNC;
import static domain.symbols.ClassType.CLS_VAR;
import static domain.symbols.TypeBase.*;
import static token.TokenType.*;
import static utils.Utils.cast;
import static utils.Utils.getArithType;

/**
 * Created by octav on 3/29/2017.
 *
 * Class that takes the resulted tokens from the {@link token.analyzer.TokenAnalyzer}
 * and verifies the syntax
 */
public class SyntaxAnalyzer {
    private static final Logger LOG = LoggerFactory.getLogger(SyntaxAnalyzer.class);

    private static SyntaxAnalyzer SINGLETON;

    private List<Token> tokens;
    private Iterator<Token> tokenIterator;
    private Token token;
    private DomainAnalyzer domainAnalyzer;

    public SyntaxAnalyzer(List<Token> tokens) {
        this.tokens = tokens;
        this.tokenIterator = tokens.iterator();
        this.domainAnalyzer = new DomainAnalyzer();
        getNext();
        SINGLETON = this;
    }

    public static SyntaxAnalyzer getInstance() {
        if(SINGLETON == null) {
            throw new RuntimeException("The syntax analyzer hasn't been initialised yet");
        }

        return SINGLETON;
    }

    private boolean getNext() {
        if(tokenIterator.hasNext()) {
            token = tokenIterator.next();
            return true;
        }
        return false;
    }

    public Token getCurrentToken() {
        return this.token;
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
        domainAnalyzer.addExtFuncs();
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
                        if(returnValue.getType().getTypeBase() == TB_STRUCT) throw new InvalidStatementException("A structure cannot be logically tested", token.getLine());

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
                        if(returnValue.getType().getTypeBase() == TB_STRUCT) throw new InvalidStatementException("A structure cannot be logically tested", token.getLine());

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
                            if (returnValue.getType().getTypeBase() == TB_STRUCT)
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
                ReturnValue returnValueA = exprAssign();
                if (returnValueA != null) {
                    if(!returnValue.isLVal())
                        throw new InvalidStatementException("Cannot assign to a non lval", token.getLine());
                    if(returnValue.getType().getNoOfElements() > -1 || returnValueA.getType().getNoOfElements() > -1)
                        throw new InvalidStatementException("Arrays cannot be assigned", token.getLine());
                    cast(returnValue.getType(), returnValueA.getType());
                    returnValue.setCtVal(false);
                    returnValue.setLVal(false);
                    return returnValue;
                } else {
                    throw new InvalidStatementException("Missing assign expression", token.getLine());
                }
            }
            //Go back since there's nothing else in here
            goBackTo(currentToken);
        }
        goBackTo(currentToken);
        returnValue = exprOr();
        if (returnValue != null) {
            return returnValue;
        }
        goBackTo(currentToken);
        return null;
    }

    public ReturnValue exprOr() {
        ReturnValue returnValue = exprAnd();
        if(returnValue != null) {
            return exprOr1(returnValue);
        }
        return null;
    }

    public ReturnValue exprOr1(ReturnValue returnValue) {
        if(token.getCode() == OR) {
            getNext();
            ReturnValue returnValueAnd = exprAnd();
            if(returnValueAnd != null) {
                if(returnValue.getType().getTypeBase() == TB_STRUCT || returnValueAnd.getType().getTypeBase() == TB_STRUCT)
                    throw new InvalidStatementException("A structure cannot be logically tested", token.getLine());
                returnValue.setType(new Type(TB_INT, -1));
                returnValue.setCtVal(false);
                returnValue.setLVal(false);
                return exprOr1(returnValue);
            } else {
                throw new InvalidStatementException("Missing statement", token.getLine());
            }
        }
        return returnValue;
    }

    public ReturnValue exprAnd() {
        ReturnValue returnValue = exprEq();
        if(returnValue != null) {
            return exprAnd1(returnValue);
        }
        return null;
    }

    public ReturnValue exprAnd1(ReturnValue returnValue) {
        if(token.getCode() == AND) {
            getNext();
            ReturnValue returnValueEq = exprEq();
            if(returnValueEq != null) {
                if(returnValue.getType().getTypeBase() == TB_STRUCT|| returnValueEq.getType().getTypeBase() == TB_STRUCT)
                    throw new InvalidStatementException("A structure cannot be logically tested", token.getLine());
                returnValue.setType(new Type(TB_INT, -1));
                returnValue.setCtVal(false);
                returnValue.setLVal(false);
                return exprAnd1(returnValue);
            } else {
                throw new InvalidStatementException("Missing expression", token.getLine());
            }
        }
        return returnValue;
    }

    public ReturnValue exprEq() {
        ReturnValue returnValue = exprRel();
        if(returnValue != null) {
            return exprEq1(returnValue);
        }
        return null;
    }

    public ReturnValue exprEq1(ReturnValue returnValue) {
        if(token.getCode() == EQUAL || token.getCode() == NOTEQ) {
            getNext();
            ReturnValue returnValueRel = exprRel();
            if(returnValueRel != null) {
                if(returnValue.getType().getTypeBase() == TB_STRUCT || returnValueRel.getType().getTypeBase() == TB_STRUCT)
                    throw new InvalidStatementException("A structure cannot be compared", token.getLine());
                returnValue.setType(new Type(TB_INT, -1));
                returnValue.setCtVal(false);
                returnValue.setLVal(false);
                return exprEq1(returnValue);
            } else {
                throw new InvalidStatementException("Missing expression", token.getLine());
            }
        }
        return returnValue;
    }

    public ReturnValue exprRel() {
        ReturnValue returnValue = exprAdd();
        if(returnValue != null) {
            return exprRel1(returnValue);
        }
        return null;
    }

    public ReturnValue exprRel1(ReturnValue returnValue) {
        if(token.getCode() == LESS || token.getCode() == LESSEQ || token.getCode() == GREATER
                || token.getCode() == GREATEREQ) {
            getNext();
            ReturnValue returnValueAdd = exprAdd();
            if(returnValueAdd != null) {
                if(returnValue.getType().getNoOfElements() >- 1 || returnValueAdd.getType().getNoOfElements() > -1)
                    throw new InvalidStatementException("An array cannot be compared", token.getLine());
                if(returnValue.getType().getTypeBase() == TB_STRUCT || returnValueAdd.getType().getTypeBase() == TB_STRUCT)
                    throw new InvalidStatementException("A structure cannot be compared", token.getLine());
                returnValue.setType(new Type(TB_INT, -1));
                returnValue.setCtVal(false);
                returnValue.setLVal(false);
                return exprRel1(returnValue);
            } else {
                throw new InvalidStatementException("Missing expression", token.getLine());
            }
        }
        return returnValue;
    }

    public ReturnValue exprAdd() {
        ReturnValue returnValue = exprMul();
        if(returnValue != null) {
            return exprAdd1(returnValue);
        }
        return null;
    }

    public ReturnValue exprAdd1(ReturnValue returnValue) {
        if(token.getCode() == ADD || token.getCode() == SUB) {
            getNext();
            ReturnValue returnValueMul = exprMul();
            if(returnValueMul != null) {
                if(returnValue.getType().getNoOfElements() >- 1 || returnValueMul.getType().getNoOfElements() > -1)
                    throw new InvalidStatementException("An array cannot be added/subtracted", token.getLine());
                if(returnValue.getType().getTypeBase() == TB_STRUCT || returnValueMul.getType().getTypeBase() == TB_STRUCT)
                    throw new InvalidStatementException("A structure cannot be added/subtracted", token.getLine());
                returnValue.setType(getArithType(returnValue.getType(), returnValueMul.getType()));
                returnValue.setCtVal(false);
                returnValue.setLVal(false);
                return exprAdd1(returnValue);
            } else {
                throw new InvalidStatementException("Missing expression after (+/-)", token.getLine());
            }
        }
        return returnValue;
    }

    public ReturnValue exprMul() {
        ReturnValue returnValue = exprCast();
        if(returnValue != null) {
            return exprMul1(returnValue);
        }
        return null;
    }

    public ReturnValue exprMul1(ReturnValue returnValue) {
        if(token.getCode() == MUL || token.getCode() == DIV) {
            getNext();
            ReturnValue returnValueCast = exprCast();
            if(returnValueCast != null) {
                if(returnValue.getType().getNoOfElements() >- 1 || returnValueCast.getType().getNoOfElements() > -1)
                    throw new InvalidStatementException("An array cannot be divided/multiplied", token.getLine());
                if(returnValue.getType().getTypeBase() == TB_STRUCT || returnValueCast.getType().getTypeBase() == TB_STRUCT)
                    throw new InvalidStatementException("A structure cannot be divided/multiplied", token.getLine());
                returnValue.setType(getArithType(returnValue.getType(), returnValueCast.getType()));
                returnValue.setCtVal(false);
                returnValue.setLVal(false);
                return exprMul1(returnValue);
            } else {
                throw new InvalidStatementException("Missing expression ", token.getLine());
            }
        }
        return returnValue;
    }

    public ReturnValue exprCast() {
        Token currentToken = token;
        if(token.getCode() == LPAR) {
            getNext();
            Type type = typeName(new Type());
            if(type != null) {
                if(token.getCode() == RPAR) {
                    getNext();
                    ReturnValue returnValue = exprCast();
                    if(returnValue != null) {
                        cast(type, returnValue.getType());
                        returnValue.setType(type);
                        returnValue.setCtVal(false);
                        returnValue.setLVal(false);
                        return returnValue;
                    }
                }
            }
            goBackTo(currentToken);
        }

        return exprUnary();
    }

    public ReturnValue exprUnary() {
        Token currentToken = token;
        if(token.getCode() == SUB || token.getCode() == NOT) {
            getNext();
            ReturnValue returnValue = exprUnary();
            if(returnValue != null) {
                if(currentToken.getCode() == SUB) {
                    if(returnValue.getType().getNoOfElements() >= 0) throw new InvalidStatementException("Unary '-' cannot be applied to an array", token.getLine());
                    if(returnValue.getType().getTypeBase() == TB_STRUCT)
                        throw new InvalidStatementException("Unary '-' cannot be applied to a structure", token.getLine());
                } else {
                    if(returnValue.getType().getTypeBase() == TB_STRUCT) throw new InvalidStatementException("Unary '!' cannot be applied to a structure", token.getLine());
                    returnValue.setType(new Type(TB_INT, -1));
                }
                returnValue.setCtVal(false);
                returnValue.setLVal(false);
                return returnValue;
            }
            goBackTo(currentToken);
        }


        return exprPostfix();
    }

    public ReturnValue exprPostfix() {
        ReturnValue returnValue = exprPrimary();
        if(returnValue != null) {
            return exprPostfix1(returnValue);
        }
        return null;
    }

    public ReturnValue exprPostfix1(ReturnValue returnValue) {
        if(token.getCode() == LBRACKET) {
            getNext();
            ReturnValue returnValueExpr = expr();
            if(returnValueExpr != null) {
                if(returnValue.getType().getNoOfElements() < 0) throw new InvalidStatementException("Only an array can be indexed", token.getLine());
                Type type = new Type(TB_INT, -1);
                cast(type, returnValueExpr.getType());
                returnValue.setType(returnValue.getType());
                returnValue.getType().setNoOfElements(-1);
                returnValue.setLVal(true);
                returnValue.setCtVal(false);
                if(token.getCode() == RBRACKET) {
                    getNext();
                    return exprPostfix1(returnValue);
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
                String name = token.getRawValue();
                StructSymbol symbol = (StructSymbol) ((StructType) returnValue.getType()).getSymbol();
                Symbol member = domainAnalyzer.findSymbol(symbol.getMembers(), name);
                if(member == null)
                    throw new InvalidStatementException("Struct" + symbol.getName() + " doesn't have a member " + name, token.getLine());
                returnValue.setType(member.getType());
                returnValue.setLVal(true);
                returnValue.setCtVal(false);
                getNext();
                return exprPostfix1(returnValue);
            } else {
                throw new InvalidStatementException("Missing ID", token.getLine());
            }
        }
        return returnValue;
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

    public ReturnValue exprPrimary() {
        ReturnValue returnValue = new ReturnValue();
        if(token.getCode() == ID) {
            Symbol symbol;
            String name = token.getRawValue();
            if((symbol = domainAnalyzer.findSymbol(name)) == null) throw new InvalidStatementException("Undefined symbol `" + name + "`", token.getLine());
            if(symbol.getType() instanceof StructType) {
                returnValue.setType(new StructType((StructType) symbol.getType()));   
            } else {
                returnValue.setType(new Type(symbol.getType()));
            }
            returnValue.setLVal(true);
            returnValue.setCtVal(false);
            getNext();
            if(token.getCode() == LPAR) {
                if(symbol.getCls() != CLS_FUNC && symbol.getCls() != CLS_EXTFUNC)
                    throw new InvalidStatementException("Call of the non-function " + name, token.getLine());
                List<Symbol> funcArgs = ((FuncSymbol) symbol).getArgs();
                int argIndex = 0;
                getNext();
                ReturnValue returnValueExpr = expr();
                if(returnValueExpr != null) {
                    if(argIndex > funcArgs.size()) throw new InvalidStatementException("Too many arguments in call", token.getLine());
                    cast(funcArgs.get(argIndex).getType(), returnValueExpr.getType());
                    argIndex++;
                    
                    while (token.getCode() == COMMA) {
                        getNext();
                        ReturnValue returnValueExpr1 = expr();
                        
                        if(argIndex > funcArgs.size()) throw new InvalidStatementException("Too many arguments in call", token.getLine());
                        cast(funcArgs.get(argIndex).getType(), returnValueExpr1.getType());
                        argIndex++;
                    }
                }
                if(token.getCode() == RPAR) {
                    if(argIndex < funcArgs.size()) throw new InvalidStatementException("Too few arguments in call", token.getLine());
                    returnValue.setType(symbol.getType());
                    returnValue.setLVal(false);
                    returnValue.setCtVal(false);
                    getNext();
                    return returnValue;
                } else {
                    throw new InvalidStatementException("Missing expression", token.getLine());
                }
            }
            if(symbol.getCls() == CLS_FUNC || symbol.getCls() == CLS_EXTFUNC)
                throw new InvalidStatementException("Missing call for function " + name, token.getLine());
            return returnValue;
        } else if(token.getCode() == CT_CHAR) {
            returnValue.setType(new Type(TB_CHAR, -1));
            returnValue.setConstantValue(token.getRawValue().charAt(0));
            returnValue.setCtVal(true);
            returnValue.setLVal(false);
            getNext();
            return returnValue;
        } else if(token.getCode() == CT_INT) {
            returnValue.setType(new Type(TB_INT, -1));
            returnValue.setConstantValue(Integer.valueOf(token.getRawValue()));
            returnValue.setCtVal(true);
            returnValue.setLVal(false);
            getNext();
            return returnValue;
        } else if (token.getCode() == CT_REAL) {
            returnValue.setType(new Type(TB_DOUBLE, -1));
            returnValue.setConstantValue(Double.valueOf(token.getRawValue()));
            returnValue.setCtVal(true);
            returnValue.setLVal(false);
            getNext();
            return returnValue;
        } else if (token.getCode() == CT_STRING) {
            returnValue.setType(new Type(TB_CHAR, 0));
            returnValue.setConstantValue(token.getRawValue());
            returnValue.setCtVal(true);
            returnValue.setLVal(false);
            getNext();
            return returnValue;
        } else if(token.getCode() == LPAR) {
            getNext();
            ReturnValue returnValueExpr = expr();
            if(returnValueExpr != null) {
                if(token.getCode() == RPAR) {
                    getNext();
                    return returnValueExpr;
                }
            }
        }
        return null;
    }
}

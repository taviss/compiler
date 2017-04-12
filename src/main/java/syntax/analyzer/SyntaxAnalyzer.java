package syntax.analyzer;

import domain.analyzer.DomainAnalyzer;
import domain.symbols.ClassType;
import domain.symbols.StructSymbol;
import domain.symbols.TypeBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import token.Token;
import token.TokenType;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
        if(typeBase() != null) {
            if(token.getCode() == MUL) {
                getNext();
            }

            if(token.getCode() == ID) {
                getNext();
                if(token.getCode() == LPAR) {
                    getNext();
                    if(funcArg()) {
                        while(token.getCode() == COMMA) {
                            getNext();
                            funcArg();
                        }
                    }
                    if(token.getCode() == RPAR) {
                        getNext();
                        if(stmCompound()) {
                            return true;
                        } else throw new InvalidStatementException("Missing closing `}` or invalid statement", token.getLine());
                    } else throw new InvalidStatementException("Missing closing `)` or invalid statement", token.getLine());
                }
            }
        }

        if(token.getCode() == VOID) {
            getNext();
            if(token.getCode() == ID) {
                getNext();
                if (token.getCode() == LPAR) {
                    getNext();
                    if (funcArg()) {
                        while (token.getCode() == COMMA) {
                            getNext();
                            funcArg();
                        }
                    }
                    if (token.getCode() == RPAR) {
                        getNext();
                        if (stmCompound()) {
                            return true;
                        } else throw new InvalidStatementException("Missing closing `}` or invalid statement", token.getLine());
                    } else throw new InvalidStatementException("Missing closing `)` or invalid statement", token.getLine());
                }
            }
        }
        goBackTo(currentToken);
        return false;
    }

    public boolean stmCompound() {
        if(token.getCode() == LACC) {
            getNext();
            while(declVar() || stm()) {

            }
            if(token.getCode() == RACC) {
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
                    if (expr()) {
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
                    if (expr()) {
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
                        expr();
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
                expr();
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
        TypeBase typeBase;
        if((typeBase = typeBase()) != null) {
            if(token.getCode() == ID) {
                getNext();
                arrayDecl();
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
                getNext();
                if (token.getCode() == LACC) {
                    String tokenText = token.getRawValue();
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

    public TypeBase typeBase() {
        if(token.getCode() == INT || token.getCode() == DOUBLE || token.getCode() == CHAR) {
            TokenType tokenType = token.getCode();
            getNext();
            return TypeBase.valueOf("TB_" + tokenType.toString());
        }

        if(token.getCode() == STRUCT) {
            TokenType tokenType = token.getCode();
            getNext();
            if(token.getCode() == ID) {
                getNext();
                return TypeBase.valueOf("TB_" + tokenType.toString());
            } else throw new InvalidStatementException("Missing identifier", token.getLine());
        }
        return null;
    }

    public boolean declVar() {
        TypeBase typeBase;
        if((typeBase = typeBase()) != null) {
            if (token.getCode() == ID) {
                String tokenID = token.getRawValue();
                getNext();
                int noOfElements = arrayDecl();
                while (token.getCode() == COMMA) {
                    getNext();
                    if (token.getCode() == ID) {
                        getNext();
                        arrayDecl();
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

    public int arrayDecl() {
        if(token.getCode() == LBRACKET) {
            getNext();
            expr();
            if(token.getCode() == RBRACKET) {
                getNext();
                return ;
            } else {
                throw new InvalidStatementException("Missing closing `}`", token.getLine());
            }
        }
        return false;
    }

    public boolean expr() {
        return exprAssign();
    }

    public boolean exprAssign() {
        Token currentToken = token;
        if(exprUnary()) {
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
            if(typeName()) {
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

    public boolean typeName() {
        if(typeBase() != null) {
            arrayDecl();
            return true;
        }
        return false;
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
        if(exprPrimary() != null) {
            return exprPostfix1();
        }
        return false;
    }

    public String exprPostfix1() {
        if(token.getCode() == LBRACKET) {
            getNext();
            Double exprResult;
            if((exprResult = expr()) != null) {
                if(token.getCode() == RBRACKET) {
                    getNext();
                    String postResult = exprPostfix1();
                    return "".equals(postResult) ? exprResult
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
            }
        }
        return "";
    }

    public ConsumedResult exprPrimary() {
        ConsumedResult consumedResult = new ConsumedResult();
        if(token.getCode() == ID) {
            getNext();
            consumedResult.validate();
            if(token.getCode() == LPAR) {
                getNext();
                expr();
                while(token.getCode() == COMMA) {
                    getNext();
                    expr();
                }
                if(token.getCode() == RPAR) {
                    getNext();
                    return consumedResult;
                } else {
                    throw new InvalidStatementException("Missing expression", token.getLine());
                }
            }
            return consumedResult;
        } else if(token.getCode() == CT_CHAR || token.getCode() == CT_INT || token.getCode() == CT_REAL
                || token.getCode() == CT_STRING) {
            consumedResult.validate();
            switch token.getCode()
            getNext();

            return tokenText;
        } else if(token.getCode() == LPAR) {
            getNext();
            if(expr()) {
                if(token.getCode() == RPAR) {
                    getNext();
                    return "";
                }
            }
        }
        return null;
    }
}

package syntax.analyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import token.Token;

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

    public SyntaxAnalyzer(List<Token> tokens) {
        this.tokens = tokens;
        this.tokenIterator = tokens.iterator();
        getNext();
    }

    private void logError(String error) {
        LOG.error(error + " @ " + token.getLine());
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
            if(!unit()) logError("No unit found.");
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
        if(typeBase()) {
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
                        } else logError("Missing statement");
                    } else logError("Missing closing `)`");
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
                        } else logError("Missing statement");
                    } else logError("Missing closing `)`");
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
            } else logError("Missing closing `}`");
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
                                    } else logError("Missing statement");
                                } else return true;
                            } else logError("Missing statement");
                        } else logError("Missing closing `)`");
                    } else logError("Missing expression in if");
                } else logError("Missing opening `(`");
                return false;
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
                            } else logError("Missing statement");
                        } else logError("Missing closing `)`");
                    } else logError("Missing expression in while");
                } else logError("Missing opening `(`");
                return false;
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
                                } else logError("Missing statement");
                            } else logError("Missing closing `)`");
                        } else logError("Missing `;`");
                    } else logError("Missing `;`");
                } else logError("Missing opening `(`");
                return false;
            }
            case BREAK: {
                getNext();
                if(token.getCode() == SEMICOLON) {
                    getNext();
                    return true;
                } else logError("Missing `;`");
                return false;
            }
            case RETURN: {
                getNext();
                expr();
                if(token.getCode() == SEMICOLON) {
                    getNext();
                    return true;
                } else logError("Missing `;`");
                return false;
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
        if(typeBase()) {
            if(token.getCode() == ID) {
                getNext();
                arrayDecl();
                return true;
            } else logError("Missing identifier");
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
                    getNext();
                    while(declVar()) {
                    }
                    if(token.getCode() == RACC) {
                        getNext();
                        if(token.getCode() == SEMICOLON) {
                            getNext();
                            return true;
                        } else {
                            logError("Missing `;`");
                            return false;
                        }
                    } else {
                        logError("Missing closing '}");
                        return false;
                    }
                }
            }
            goBackTo(currentToken);
        }
        return false;
    }

    public boolean typeBase() {
        if(token.getCode() == INT || token.getCode() == DOUBLE || token.getCode() == CHAR) {
            getNext();
            return true;
        }

        if(token.getCode() == STRUCT) {
            getNext();
            if(token.getCode() == ID) {
                getNext();
                return true;
            } else logError("Missing identifier");
        }
        return false;
    }

    public boolean declVar() {
        if(typeBase()) {
            if (token.getCode() == ID) {
                getNext();
                arrayDecl();
                while (token.getCode() == COMMA) {
                    getNext();
                    if (token.getCode() == ID) {
                        getNext();
                        arrayDecl();
                    } else {
                        logError("Missing identifier");
                        return false;
                    }
                }
                if (token.getCode() == SEMICOLON) {
                    getNext();
                    return true;
                } else {
                    logError("Missing `;`");
                    return false;
                }
            } else {
                logError("Missing identifier");
                return false;
            }
        }
        return false;
    }

    public boolean arrayDecl() {
        if(token.getCode() == LBRACKET) {
            getNext();
            expr();
            if(token.getCode() == RBRACKET) {
                getNext();
                return true;
            } else {
                logError("Missing closing `}`");
                return false;
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
                    logError("Missing assign expression");
                    return false;
                }
            }
            //Go back since there's nothing else in here
            goBackTo(currentToken);
        }

        return exprOr();
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
                logError("Missing statement");
                return false;
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
        if(typeBase()) {
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
            }
        }
        return true;
    }

    public boolean exprEq1() {
        if(token.getCode() == EQUAL || token.getCode() == NOTEQ) {
            getNext();
            if(exprRel()) {
                exprEq1();
            }
        }
        return true;
    }

    public boolean exprAnd1() {
        if(token.getCode() == AND) {
            getNext();
            if(exprEq()) {
                exprAnd1();
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
                    exprPostfix1();
                }
            }
        }
        if(token.getCode() == DOT) {
            getNext();
            if(token.getCode() == ID) {
                getNext();
                exprPostfix1();
            }
        }
        return true;
    }

    public boolean exprPrimary() {
        if(token.getCode() == ID) {
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

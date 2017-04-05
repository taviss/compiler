package syntax.analyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import token.Token;

import java.util.Iterator;
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

    public void start() {
        while(tokenIterator.hasNext()) {
            if(!unit()) logError("No unit found.");
        }
    }

    public boolean unit() {
        if(declStruct()) return true;
        return false;
    }

    public boolean declStruct() {
        if(token.getCode() == STRUCT) {
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
                            logError("Expected `;`");
                            return false;
                        }
                    } else {
                        logError("Expected '}");
                        return false;
                    }
                } else {
                    logError("Expected `LACC`");
                    return false;
                }
            } else {
                logError("Expected `ID`");
                return false;
            }
        }
        return false;
    }

    public boolean typeBase() {
        if(token.getCode() == INT || token.getCode() == DOUBLE || token.getCode() == CHAR) {
            getNext();
            return true;
        }
        logError("Expected type");
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
                        logError("Expected `ID`");
                        return false;
                    }
                }
                if (token.getCode() == SEMICOLON) {
                    getNext();
                    return true;
                } else {
                    logError("Expected `;`");
                    return false;
                }
            } else {
                logError("Expected `ID`");
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
                return true;
            } else {
                logError("Expected `}`");
                return false;
            }
        }
        return false;
    }

    public boolean expr() {
        return exprAssign();
    }

    public boolean exprAssign() {
        if(exprUnary()) {
            if (token.getCode() == ASSIGN) {
                getNext();
                if (exprAssign() || exprOr()) {
                    return true;
                } else {
                    logError("Expected `exprAssign` or `epxrOr");
                    return false;
                }
            } else {
                logError("Expected `=`");
                return false;
            }
        }
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
                if(exprOr1()) {
                    return true;
                } else {
                    logError("Expected `exprOr'`");
                    return false;
                }
            } else {
                logError("Expected `exprAnd`");
                return false;
            }
        }
        return false;
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
                if(exprMul1()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean exprCast() {
        if(token.getCode() == LPAR) {
            getNext();
            if(typeName()) {
                if(token.getCode() == RPAR) {
                    if(exprCast() || exprUnary()) {
                        return true;
                    }
                }
            }
        }
        return false;
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
                if(exprAdd1()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean exprRel1() {
        if(token.getCode() == LESS || token.getCode() == LESSEQ || token.getCode() == GREATER
                || token.getCode() == GREATEREQ) {
            getNext();
            if(exprAdd()) {
                if(exprRel1()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean exprEq1() {
        if(token.getCode() == EQUAL || token.getCode() == NOTEQ) {
            getNext();
            if(exprRel()) {
                if(exprEq1()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean exprAnd1() {
        if(token.getCode() == AND) {
            getNext();
            if(exprEq()) {
                if(exprAnd1()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean exprUnary() {
        if(token.getCode() == SUB || token.getCode() == NOT) {
            getNext();
            if(exprPostfix()) {
                return exprUnary1();
            }
        }
        return false;
    }

    public boolean exprUnary1() {
        if(exprPostfix()) {
            if(exprUnary1()) {
                return true;
            }
        }
        return false;
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
                    return true;
                }
            }
        }
        if(token.getCode() == DOT) {
            getNext();
            if(token.getCode() == ID) {
                return true;
            }
        }
        return false;
    }

    public boolean exprPrimary() {
        if(token.getCode() == ID) {
            getNext();
            if(token.getCode() == LPAR) {
                getNext();
                expr();
                while(token.getCode() == COMMA) {
                    expr();
                }
                if(token.getCode() == RPAR) {
                    return true;
                }
            }
            return true;
        } else if(token.getCode() == CT_CHAR || token.getCode() == CT_INT || token.getCode() == CT_REAL
                || token.getCode() == CT_STRING) {
            return true;
        } else if(token.getCode() == LPAR) {
            if(expr()) {
                if(token.getCode() == RPAR) {
                    return true;
                }
            }
        }
        return false;
    }
}

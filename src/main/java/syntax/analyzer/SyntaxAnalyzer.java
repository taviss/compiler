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

    public void unit() {
        if(token.getCode() == STRUCT) {
            getNext();
            declStruct();
        }
    }

    public void declStruct() {
        if(token.getCode() == ID) {
            getNext();
            if(token.getCode() == LACC) {
                getNext();
                while(token.getCode() == INT || token.getCode() == DOUBLE || token.getCode() == CHAR) {
                    getNext();
                    declVar();
                }
            } else logError("Expected `LACC`");
        } else logError("Expected `ID`");
    }

    public void declVar() {
        if(token.getCode() == ID) {
            getNext();
            if(token.getCode() == LBRACKET) {
                getNext();
                arrayDecl();
            }
            while(token.getCode() == COMMA) {
                getNext();
                if(token.getCode() == ID) {
                    getNext();
                    if(token.getCode() == LBRACKET) {
                        getNext();
                        arrayDecl();
                    }
                } else logError("Expected `,`");
            }
            if(token.getCode() == SEMICOLON) {
                getNext();
            } else logError("Expected `;`");
        }
    }

    public void arrayDecl() {

    }
}

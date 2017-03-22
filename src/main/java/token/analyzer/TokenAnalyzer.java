package token.analyzer;

import definition.DefinitionEntry;
import definition.Definitions;
import token.TextToken;
import token.Token;
import token.TokenType;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by octav on 3/15/2017.
 */
public class TokenAnalyzer {

    private Definitions definitions;

    public List<Token> analyzeTextToken(int line, String token) {
        List<Token> tokens = new LinkedList<>();

        for(DefinitionEntry definitionEntry : definitions.getDefinitionEntries()) {
            Pattern pattern = Pattern.compile(definitionEntry.getRegEx());
            Matcher matcher = pattern.matcher(token);

            while (matcher.find()) {
                switch(definitionEntry.getType()) {
                    case ID: {
                        TextToken textToken = new TextToken(TokenType.ID, line, matcher.group());
                        tokens.add(textToken);
                        break;
                    }
                    case "double": {
                        TextToken textToken = new TextToken(TokenType., line, matcher.group());
                        tokens.add(textToken);
                        break;
                    }
                    case "long": {
                        TextToken textToken = new TextToken(TokenType.ID, line, matcher.group());
                        tokens.add(textToken);
                        break;
                    }
                }
            }
        }

        /*
        for(char c : token.toCharArray()) {
            switch(c) {
                case ''
            }
        }*/
        return tokens;
    }

    public Definitions getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Definitions definitions) {
        this.definitions = definitions;
    }
}

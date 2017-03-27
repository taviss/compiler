package token.analyzer;

import definition.DefinitionEntry;
import definition.Definitions;
import token.*;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by octav on 3/15/2017.
 */
public class TokenAnalyzer {

    private Definitions definitions;

    public List<Token> analyzeTextToken(int line, String token) {
        List<Token> tokenList = new LinkedList<>();
        String currentText = token.trim();

        for(DefinitionEntry definitionEntry : definitions.getDefinitionEntries()) {
            Pattern pattern = Pattern.compile(definitionEntry.getRegEx().trim());
            Matcher matcher = pattern.matcher(currentText);

            while (matcher.find()) {
                if(definitionEntry.getType().equals("ignore")) {
                    continue;
                }

                boolean addToken = true;
                for(Token tokenIn : tokenList) {
                    DefinitionEntry tokenDefinition = definitions.getByName(tokenIn.getCode());
                    String foundString = matcher.group();

                    if(tokenIn.getRawValue().contains(foundString)) {
                        int index = tokenIn.getRawValue().indexOf(foundString);
                        //If they are the same ones we're talking about
                        if(currentText.indexOf(tokenIn.getRawValue()) == currentText.indexOf(foundString) + index) {
                            addToken = false;
                        }
                    } else if(foundString.contains(tokenIn.getRawValue())) {
                        int index = foundString.indexOf(tokenIn.getRawValue());
                        //If they are the same ones we're talking about
                        if(currentText.indexOf(tokenIn.getRawValue()) == currentText.indexOf(foundString) - index) {

                            //TODO
                            //First check if string, else check by priority
                            if(tokenIn.getCode() == TokenType.CT_STRING || definitionEntry.getName() == TokenType.CT_STRING) {
                                if (tokenIn.getCode() == TokenType.CT_STRING) {
                                    tokenList.remove(tokenIn);
                                } else if (definitionEntry.getName() == TokenType.CT_STRING) {
                                    continue;
                                }
                            } else {
                                if (tokenDefinition != null && tokenDefinition.getPriority() < definitionEntry.getPriority()) {
                                    addToken = false;
                                } else {
                                    tokenList.remove(tokenIn);
                                }
                            }
                        }
                    }


                    //If it exists and priority >, don't add new, else remove old one
                    if((tokenIn.getRawValue().contains(foundString) && ) || foundString.contains(tokenIn.getRawValue())) {
                        if(tokenIn.getCode() == TokenType.CT_STRING) {
                            continue;
                        } else if(definitionEntry.getName() == TokenType.CT_STRING) {

                        }

                        if(tokenDefinition != null && tokenDefinition.getPriority() < definitionEntry.getPriority()) {
                            addToken = false;
                        } else {
                            tokenList.remove(tokenIn);
                        }
                        break;
                    }
                }

                if(addToken) {
                    switch (definitionEntry.getType()) {
                        case "text": {
                            TextToken textToken = new TextToken(definitionEntry.getName(), line, matcher.group());
                        /*
                        String replaceString = Matcher.quoteReplacement(matcher.group());
                        currentText =  currentText.replace(replaceString, "");
                        */
                            tokenList.add(textToken);
                            break;
                        }
                        case "double": {
                            DoubleToken doubleToken = new DoubleToken(definitionEntry.getName(), line, Double.valueOf(matcher.group()));
                        /*
                        String replaceString = Matcher.quoteReplacement(matcher.group());
                        currentText =  currentText.replace(replaceString, "");
                        */
                            tokenList.add(doubleToken);
                            break;
                        }
                        case "long": {
                            LongToken longToken = new LongToken(definitionEntry.getName(), line, Integer.valueOf(matcher.group()));
                        /*
                        String replaceString = Matcher.quoteReplacement(matcher.group());
                        currentText =  currentText.replace(replaceString, "");
                        */
                            tokenList.add(longToken);
                            break;
                        }
                        default:
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
        sortTokens(currentText, tokenList);
        return tokenList;
    }

    public void sortTokens(String initialToken, List<Token> resultedTokens) {
        resultedTokens.sort(new Comparator<Token>() {
            @Override
            public int compare(Token o1, Token o2) {
                return initialToken.indexOf(o1.getRawValue()) - initialToken.indexOf(o2.getRawValue());
            }
        });
    }

    public Definitions getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Definitions definitions) {
        this.definitions = definitions;
    }
}

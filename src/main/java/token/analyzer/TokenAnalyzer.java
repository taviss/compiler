package token.analyzer;

import definition.DefinitionEntry;
import definition.Definitions;
import token.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by octav on 3/15/2017.
 */

/**
 * Class for finding tokens in strings
 */
public class TokenAnalyzer {

    /**
     * The list of definitions(token types) to be used in the analysis
     */
    private Definitions definitions;

    //TODO Use allowed characters in variable names rather than these delimiters?
    private Character[] delimiters = {' ', '(', ')', '{', '}', '\t', '\n', '[', ']', ';', ',', '.'};

    /**
     * Takes a string and finds all valid tokens
     * @param line The line in the source file, passed by {@link file.parser.SourceParser}
     * @param token The actual string to be analyzed
     * @return A list of tokens
     */
    public List<Token> analyzeTextToken(int line, String token) {
        List<Token> tokenList = new LinkedList<>();
        String currentText = token.trim();

        for(DefinitionEntry definitionEntry : definitions.getDefinitionEntries()) {
            Pattern pattern = Pattern.compile(definitionEntry.getRegEx().trim());
            Matcher matcher = pattern.matcher(currentText);

            if(definitionEntry.getType().equals("ignore") && definitionEntry.getName() == TokenType.SPACE) {
                continue;
            }

            while (matcher.find()) {
                String foundString = matcher.group();

                //This prevents keywords from getting matched when used in IDs
                if(definitionEntry.getPriority() == 0) {
                    int before = matcher.start() - 1;
                    int after = matcher.end();
                    if ((matcher.start() > 0 && !Arrays.asList(delimiters).contains(currentText.charAt(before))) || (matcher.end() < currentText.length() - 1 && !Arrays.asList(delimiters).contains(currentText.charAt(after)))) {
                        continue;
                    }
                }

                boolean addToken = true;

                //Check if we already interpreted the current match and eliminate or don't add accordingly
                for(Iterator<Token> iterator = tokenList.iterator(); iterator.hasNext();) {
                    Token tokenIn = iterator.next();
                    DefinitionEntry tokenDefinition = definitions.getByName(tokenIn.getCode());

                    if(tokenIn.getRawValue().contains(foundString)) {
                        //If they are the same ones we're talking about
                        if(matcher.start() >= tokenIn.getStartMatchIndex() && matcher.end() <= tokenIn.getEndMatchIndex()) {
                            //First check if string, else check by priority
                            if(tokenIn.getCode() == TokenType.CT_STRING || tokenIn.getCode() == TokenType.CHAR) {
                                addToken = false;
                            } else {
                                if (tokenDefinition != null && tokenDefinition.getPriority() < definitionEntry.getPriority()) {
                                    addToken = false;
                                } else {
                                    iterator.remove();
                                }
                            }
                        }
                    } else if(foundString.contains(tokenIn.getRawValue())) {
                        //If they are the same ones we're talking about
                        if(tokenIn.getStartMatchIndex() >= matcher.start() && tokenIn.getEndMatchIndex() <= matcher.end()) {
                            //First check if string, else check by priority
                            if(definitionEntry.getName() == TokenType.CT_STRING || definitionEntry.getName() == TokenType.CHAR) {
                                iterator.remove();
                            } else {
                                if (tokenDefinition != null && tokenDefinition.getPriority() < definitionEntry.getPriority()) {
                                    addToken = false;
                                } else {
                                    iterator.remove();
                                }
                            }
                        }
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
                            textToken.setStartMatchIndex(matcher.start());
                            textToken.setEndMatchIndex(matcher.end());
                            tokenList.add(textToken);
                            break;
                        }
                        case "double": {
                            DoubleToken doubleToken = new DoubleToken(definitionEntry.getName(), line, Double.valueOf(matcher.group()));
                        /*
                        String replaceString = Matcher.quoteReplacement(matcher.group());
                        currentText =  currentText.replace(replaceString, "");
                        */
                            doubleToken.setStartMatchIndex(matcher.start());
                            doubleToken.setEndMatchIndex(matcher.end());
                            tokenList.add(doubleToken);
                            break;
                        }
                        case "long": {
                            long value;
                            if(foundString.contains("x")) {
                                value = Integer.decode(foundString);
                            } else if(foundString.startsWith("0")) {
                                value = Long.decode(foundString);
                            } else {
                                value = Long.valueOf(foundString);
                            }
                            LongToken longToken = new LongToken(definitionEntry.getName(), line, value);
                        /*
                        String replaceString = Matcher.quoteReplacement(matcher.group());
                        currentText =  currentText.replace(replaceString, "");
                        */
                            longToken.setStartMatchIndex(matcher.start());
                            longToken.setEndMatchIndex(matcher.end());
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
        sortTokens(tokenList);
        return tokenList;
    }

    /**
     * Method that sorts tokens by their position in the original string
     * @param resultedTokens
     */
    public void sortTokens(List<Token> resultedTokens) {
        resultedTokens.sort(new Comparator<Token>() {
            @Override
            public int compare(Token o1, Token o2) {
                return o1.getStartMatchIndex() - o2.getStartMatchIndex();
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

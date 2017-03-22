package token.analyzer;

import definition.DefinitionEntry;
import definition.Definitions;
import token.*;

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
        List<Token> tokenList = new LinkedList<>();
        String currentText = token.trim();

        for(DefinitionEntry definitionEntry : definitions.getDefinitionEntries()) {
            Pattern pattern = Pattern.compile(definitionEntry.getRegEx().trim());
            Matcher matcher = pattern.matcher(currentText);

            while (matcher.find()) {
                switch(definitionEntry.getType()) {
                    case "text": {
                        TextToken textToken = new TextToken(definitionEntry.getName(), line, matcher.group());
                        String replaceString = Matcher.quoteReplacement(matcher.group());
                        currentText =  currentText.replace(replaceString, "");
                        tokenList.add(textToken);
                        break;
                    }
                    case "double": {
                        DoubleToken doubleToken = new DoubleToken(definitionEntry.getName(), line, Double.valueOf(matcher.group()));
                        String replaceString = Matcher.quoteReplacement(matcher.group());
                        currentText =  currentText.replace(replaceString, "");
                        tokenList.add(doubleToken);
                        break;
                    }
                    case "long": {
                        LongToken longToken = new LongToken(definitionEntry.getName(), line, Integer.valueOf(matcher.group()));
                        String replaceString = Matcher.quoteReplacement(matcher.group());
                        currentText =  currentText.replace(replaceString, "");
                        tokenList.add(longToken);
                        break;
                    }
                    default:
                        break;
                }
            }
        }

        /*
        for(char c : token.toCharArray()) {
            switch(c) {
                case ''
            }
        }*/
        return tokenList;
    }

    public Definitions getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Definitions definitions) {
        this.definitions = definitions;
    }
}

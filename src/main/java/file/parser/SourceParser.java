package file.parser;

import token.Token;
import token.analyzer.TokenAnalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by octav on 3/15/2017.
 */
public class SourceParser {
    private TokenAnalyzer tokenAnalyzer;

    public List<Token> parsFile(String path) {
        List<Token> tokensFromFile = new LinkedList<>();

        try {
            File file = new File(path);
            Scanner input = new Scanner(file);

            while (input.hasNext()) {
                String nextToken = input.next();
                List<Token> resultedTokens = tokenAnalyzer.analyzeTextToken(nextToken);
                tokensFromFile.addAll(resultedTokens);
            }
        } catch(FileNotFoundException e) {
            //TODO catch
        }
        return tokensFromFile;
    }

    public void setTokenAnalyzer(TokenAnalyzer tokenAnalyzer) {
        this.tokenAnalyzer = tokenAnalyzer;
    }
}

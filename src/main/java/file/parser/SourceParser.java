package file.parser;

import token.Token;
import token.analyzer.TokenAnalyzer;

import java.io.*;
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
            LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(path));
            String line;

            while ((line = lineNumberReader.readLine()) != null) {
                Scanner scanner = new Scanner(line).useDelimiter("(?=[\\s\\(;])");

                while (scanner.hasNext()) {
                    String nextToken = scanner.next();
                    List<Token> currentTokenResultList = tokenAnalyzer.analyzeTextToken(lineNumberReader.getLineNumber(), nextToken);
                    tokensFromFile.addAll(currentTokenResultList);
                }
            }
        } catch(IOException e) {
            //TODO catch
        }
        return tokensFromFile;
    }

    public void setTokenAnalyzer(TokenAnalyzer tokenAnalyzer) {
        this.tokenAnalyzer = tokenAnalyzer;
    }
}

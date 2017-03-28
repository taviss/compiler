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

    /**
     * Takes a path to a file and reads it line by line, passing each line to the {@link TokenAnalyzer}
     * @param path
     * @return the list of resulted tokens from file
     */
    public List<Token> parsFile(String path) {
        List<Token> tokensFromFile = new LinkedList<>();

        try {
            LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(path));
            String line;

            while ((line = lineNumberReader.readLine()) != null) {
                //Ignore line comments
                if(line.startsWith("//")) {
                    continue;
                }

                List<Token> currentTokenResultList = tokenAnalyzer.analyzeTextToken(lineNumberReader.getLineNumber(), line);
                tokensFromFile.addAll(currentTokenResultList);

                /*
                Scanner scanner = new Scanner(line).useDelimiter("(?=[\\s])");

                while (scanner.hasNext()) {
                    String nextToken = scanner.next();
                    if(nextToken.contains("/*")) {
                        System.out.println();
                    }

                    if(nextToken.trim().length() == 0) {
                        continue;
                    }

                    List<Token> currentTokenResultList = tokenAnalyzer.analyzeTextToken(lineNumberReader.getLineNumber(), nextToken);

                    tokensFromFile.addAll(currentTokenResultList);

                }
                */
            }
        } catch(IOException e) {
            //TODO catch
        }
        System.out.println(tokensFromFile.toString());
        return tokensFromFile;
    }

    /**
     * TODO Move this somewhere else
     * @param fileName
     * @param tokenList
     */
    public void generateOutput(String fileName, List<Token> tokenList) {
        File file = new File(fileName);
        try {
            if(file.createNewFile()) {
                PrintWriter writer = new PrintWriter(file, "UTF-8");
                int line = 1;
                for(Token token : tokenList) {
                    if(token.getLine() > line) {
                        writer.println();
                        line = token.getLine();
                    }
                    writer.print(token.getRawValue() + " ");
                }
                writer.close();
            }

        } catch (IOException e) {
            //TODO
        }
    }

    public void setTokenAnalyzer(TokenAnalyzer tokenAnalyzer) {
        this.tokenAnalyzer = tokenAnalyzer;
    }
}

import definition.Definitions;
import definition.DefinitionsLoader;
import file.parser.SourceParser;
import syntax.analyzer.SyntaxAnalyzer;
import token.Token;
import token.analyzer.TokenAnalyzer;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by octav on 3/15/2017.
 */
public class CompDriver {
    //TODO Use logger

    public static void main(String[] args){
        SourceParser sourceParser = new SourceParser();
        //sourceParser.parsFile();
        DefinitionsLoader definitionsLoader = new DefinitionsLoader();
        Definitions definitions = definitionsLoader.loadDefinitions();
        TokenAnalyzer tokenAnalyzer = new TokenAnalyzer();
        tokenAnalyzer.setDefinitions(definitions);
        sourceParser.setTokenAnalyzer(tokenAnalyzer);

        URL defURL = CompDriver.class.getClassLoader().getResource("tests/2.c");

        List<Token> tokenList = sourceParser.parsFile(defURL.getPath());
        SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(tokenList);
        syntaxAnalyzer.start();

        /*
        //Just for testing
        for(int i = 1; i <= 9; i++) {
            System.out.println("File: " + i + ".c");
            URL defURL = CompDriver.class.getClassLoader().getResource("tests/" + i + ".c");

            List<Token> tokenList = sourceParser.parsFile(defURL.getPath());
            SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(tokenList);
            syntaxAnalyzer.start();



            new File("D:/lftc-compiler/output/tests").mkdirs();
            new File("D:/lftc-compiler/output/tests/" + i + ".c").delete();


            //FIXME This is broken now because of the removal of first and last _"_ or _'_ from the match
            //sourceParser.generateOutput("D:/lftc-compiler/output/tests/" + i + ".c", tokenList);
        }*/

        System.out.println("Fin");
    }
}

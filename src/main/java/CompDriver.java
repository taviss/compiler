import definition.Definitions;
import definition.DefinitionsLoader;
import file.parser.SourceParser;
import token.analyzer.TokenAnalyzer;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by octav on 3/15/2017.
 */
public class CompDriver {

    public static void main(String[] args){
        SourceParser sourceParser = new SourceParser();
        //sourceParser.parsFile();
        DefinitionsLoader definitionsLoader = new DefinitionsLoader();
        Definitions definitions = definitionsLoader.loadDefinitions();
        TokenAnalyzer tokenAnalyzer = new TokenAnalyzer();
        tokenAnalyzer.setDefinitions(definitions);
        sourceParser.setTokenAnalyzer(tokenAnalyzer);

        URL defURL = CompDriver.class.getClassLoader().getResource("tests/3.c");

        sourceParser.parsFile(defURL.getPath());

        System.out.println("test");
    }
}

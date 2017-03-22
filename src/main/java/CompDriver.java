import definition.Definitions;
import definition.DefinitionsLoader;
import file.parser.SourceParser;
import token.analyzer.TokenAnalyzer;

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

        sourceParser.parsFile("D:\\comp\\src\\main\\resources\\tests\\3.c");

        System.out.println("test");
    }
}

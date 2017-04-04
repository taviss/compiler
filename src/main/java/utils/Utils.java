package utils;

/**
 * Created by octav on 3/29/2017.
 */
public class Utils {
    public static String replaceCharAt(String s, int pos, char c) {
        return s.substring(0,pos) + c + s.substring(pos+1);
    }
}

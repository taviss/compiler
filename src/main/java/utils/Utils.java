package utils;

/**
 * Created by octav on 3/29/2017.
 */
public class Utils {
    public static String replaceCharAt(String s, int pos, char c) {
        return s.substring(0,pos) + c + s.substring(pos+1);
    }

    public static String fixEscapedChars(String s) {
        char[] charArray = s.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < charArray.length; i++) {
            if((charArray[i] == 'n' || charArray[i] == 't' || charArray[i] == '\"' || charArray[i] == '\\') && i > 0 && charArray[i-1] == '\\') {
                switch(charArray[i]) {
                    case 'n': stringBuilder.append('\n'); break;
                    case 't': stringBuilder.append('\t'); break;
                    case '\"': stringBuilder.append('\"'); break;
                    case '\\': stringBuilder.append('\\'); break;
                }
            } else {
                if(charArray[i] == '\\') continue;

                stringBuilder.append(charArray[i]);
            }
        }

        return stringBuilder.toString();
    }
}

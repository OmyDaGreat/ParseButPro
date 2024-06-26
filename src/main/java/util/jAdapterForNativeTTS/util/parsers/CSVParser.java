//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package util.jAdapterForNativeTTS.util.parsers;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CSVParser {
    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';

    public static List<String> parseLine(String cvsLine) {
        return parseLine(cvsLine, ',', '"');
    }

    public static List<String> parseLine(String cvsLine, char separators) {
        return parseLine(cvsLine, separators, '"');
    }

    public static List<String> parseLine(String cvsLine, char separators, char customQuote) {
        List<String> result = new ArrayList<>();
        if (cvsLine != null && !cvsLine.isEmpty()) {
            if (customQuote == ' ') {
                customQuote = DEFAULT_QUOTE;
            }

            if (separators == ' ') {
                separators = DEFAULT_SEPARATOR;
            }

            StringBuilder curVal = new StringBuilder();
            boolean inQuotes = false;
            boolean startCollectChar = false;
            boolean doubleQuotesInColumn = false;
            char[] chars = cvsLine.toCharArray();

            for (char ch : chars) {
                if (inQuotes) {
                    startCollectChar = true;
                    if (ch == customQuote) {
                        inQuotes = false;
                        doubleQuotesInColumn = false;
                    } else if (ch == '"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }
                } else if (ch == customQuote) {
                    inQuotes = true;
                    if (chars[0] != '"' && customQuote == '"') {
                        curVal.append('"');
                    }

                    if (startCollectChar) {
                        curVal.append('"');
                    }
                } else if (ch == separators) {
                    result.add(curVal.toString());
                    curVal = new StringBuilder();
                    startCollectChar = false;
                } else if (ch != '\r') {
                    if (ch == '\n') {
                        break;
                    }

                    curVal.append(ch);
                }
            }

            result.add(curVal.toString());
        }
        return result;
    }
}

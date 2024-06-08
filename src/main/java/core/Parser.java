package core;

import static java.lang.StringTemplate.STR;

public class Parser {
    private static final String CRLF = "\r\n";
    public static final String OK = "+OK\r\n";
    private static final String NULL = "_\r\n";
    public static final String NULL_BULK = "$-1\r\n";
    public static final String UNKNOWN = "unknown command";

    public static String encodeBulkStr(String str) {
        if (str == null)
            return NULL;
       return STR."$\{str.length()}\{CRLF}\{str}\{CRLF}";
    }
    public static String encodeArray(String[] array) {
        StringBuilder res = new StringBuilder(STR."*\{array.length}\{CRLF}");
        for (int i = 0; i < array.length; i++) {
            res.append(encodeBulkStr(array[i]));
        }
        return res.toString();

    }

    public static String encodeSimpleStr(String str) {
        return STR."+\{str}\{CRLF}";
    }
}

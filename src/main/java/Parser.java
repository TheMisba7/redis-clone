import static java.lang.StringTemplate.STR;

public class Parser {
    private static final String CRLF = "\r\n";

    public static String encodeBulkStr(String str) {
        if (str == null)
            return encodeNull();
       return STR."$\{str.length()}\{CRLF}\{str}\{CRLF}";
    }
    public static String encodeArray(String[] array) {
        StringBuilder res = new StringBuilder(STR."*\{array.length}\{CRLF}");
        for (int i = 1; i < array.length; i++) {
            res.append(encodeBulkStr(array[i]));
        }
        return res.toString();

    }
    public static String encodeNull() {
        return STR."_\{CRLF}";
    }
}

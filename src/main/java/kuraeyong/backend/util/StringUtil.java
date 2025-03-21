package kuraeyong.backend.util;

public class StringUtil {

    public StringUtil() {
    }

    public static String equalizeStinNmLen(String stinNm) {
        if (stinNm == null) {
            return "";
        }
        int tapCnt = switch (stinNm.length()) {
            case 1, 2 -> 7;
            case 3, 4 -> 6;
            case 5, 6, 7 -> 5;
            case 8, 9 -> 4;
            case 10, 11, 12 -> 3;
            case 13, 14 -> 2;
            default -> 1;
        };
        tapCnt = stinNm.matches(".*[0-9].*") ? tapCnt + 1 : tapCnt;

        StringBuilder sb = new StringBuilder(stinNm);
        while (tapCnt-- > 0) {
            sb.append("\t");
        }
        return sb.toString();
    }

    public static boolean isNullString(String str) {
        return str.equals("null");
    }

    // FIXME. ExceptionHandle 코드 추가
    public static int getLastNumber(String str) {
        return str.charAt(str.length() - 1) - '0';
    }
}

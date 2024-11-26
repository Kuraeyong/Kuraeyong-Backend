package kuraeyong.backend.domain.constant;

public enum DirectionType {
    UP("상행"),
    DOWN("하행"),

    // 환승 간선(TRF_EDGE)인 경우
    UP_UP("상상"),
    UP_DOWN("상하"),
    DOWN_UP("하상"),
    DOWN_DOWN("하하");

    private final String directionType;

    DirectionType(String directionType) {
        this.directionType = directionType;
    }

    public String get() {
        return directionType;
    }

    public static DirectionType convertToTrfDirectionType(DirectionType prev, DirectionType next) {
        if (prev == UP && next == UP) {
            return UP_UP;
        }
        if (prev == UP && next == DOWN) {
            return UP_DOWN;
        }
        if (prev == DOWN && next == UP) {
            return DOWN_UP;
        }
        if (prev == DOWN && next == DOWN) {
            return DOWN_DOWN;
        }
        return null;
    }

    public static String get(String lnCd, String trnNo) {
        if (trnNo == null) {
            return null;
        }
        int lastNumber = trnNo.charAt(trnNo.length() - 1) - '0';
        if (lnCd == null || !isCircularLineOfLine2(lnCd, trnNo)) {
            return determineDirection(lastNumber);
        }
        if (lastNumber % 2 == 0) {
            return "내선";
        }
        return "외선";
    }

    private static String determineDirection(int lastNumber) {
        if (lastNumber % 2 == 0) {
            return UP.get();
        }
        return DOWN.get();
    }

    private static boolean isCircularLineOfLine2(String lnCd, String trnNo) {
        return lnCd.equals("2") && trnNo.startsWith("2");
    }
}

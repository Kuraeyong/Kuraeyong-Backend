package kuraeyong.backend.domain.constant;

public enum SortType {
    NONE(-1),
    MIN_TIME(0),
    CONGESTION(1),
    TRF_CNT(2);

    private final int sortType;

    SortType(int sortType) {
        this.sortType = sortType;
    }

    public int get() {
        return sortType;
    }

    public static SortType parse(String sortType) {
        return switch (sortType) {
            case "최단시간" -> MIN_TIME;
            case "혼잡도" -> CONGESTION;
            case "최소환승" -> TRF_CNT;
            default -> NONE;
        };
    }
}

package kuraeyong.backend.domain.constant;

public enum SortType {
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
}

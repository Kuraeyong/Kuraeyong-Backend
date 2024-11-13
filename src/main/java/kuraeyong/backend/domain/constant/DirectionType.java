package kuraeyong.backend.domain.constant;

public enum DirectionType {
    UP(0),    // 상행
    DOWN(1),    // 하행

    // 환승 간선(TRF_EDGE)인 경우
    UP_UP(2),    // 상상
    UP_DOWN(3),    // 상하
    DOWN_UP(4),    // 하상
    DOWN_DOWN(5);    // 하하

    private final int directionType;

    DirectionType(int directionType) {
        this.directionType = directionType;
    }

    public int get() {
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
}

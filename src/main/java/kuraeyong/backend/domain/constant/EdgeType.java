package kuraeyong.backend.domain.constant;

public enum EdgeType {
    NONE(-1),   // 간선 종류가 중요하지 않은 경우
    GEN_EDGE(0),    // 일반 간선
    EXP_EDGE(1),    // 급행 간선
    TRF_EDGE(2);    // 노선 환승 간선 (일반 → 급행은 판정 X)

    private final int edgeType;

    private EdgeType(int edgeType) {
        this.edgeType = edgeType;
    }

    public int get() {
        return edgeType;
    }

    public static EdgeType intToEdgeType(int edgeType) {
        return switch (edgeType) {
            case 0 -> GEN_EDGE;
            case 1 -> EXP_EDGE;
            case 2 -> TRF_EDGE;
            default -> NONE;
        };
    }

    /**
     * @param prevEdgeType 현재 노드로 들어오는 간선
     * @param currEdgeType 현재 노드에서 나가는 간선
     * @return 노선 환승 여부
     */
    public static boolean checkLineTrf(EdgeType prevEdgeType, EdgeType currEdgeType) {
        return prevEdgeType == TRF_EDGE && currEdgeType != TRF_EDGE;
    }

    public static boolean checkGenExpTrf(EdgeType prevEdgeType, EdgeType currEdgeType) {
        return (prevEdgeType == GEN_EDGE && currEdgeType == EXP_EDGE) ||
                (prevEdgeType == EXP_EDGE && currEdgeType == GEN_EDGE);
    }
}

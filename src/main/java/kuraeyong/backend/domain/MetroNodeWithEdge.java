package kuraeyong.backend.domain;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MetroNodeWithEdge implements Comparable<MetroNodeWithEdge> {
    private MetroNode node;
    @Setter
    private double weight;
    @Setter
    private EdgeType edgeType;  // GEN, EXP, TRF
    @Setter
    private double waitingTime;
    @Setter
    private DirectionType direction;   // UP, DOWN
    private BranchDirectionType branchDirection;    // MAIN_TO_SUB, SUB_TO_MAIN

    public MetroNodeWithEdge(MetroNodeWithEdge node) {
        this.node = new MetroNode(node.node);
        this.weight = node.weight;
        this.edgeType = node.edgeType;
        this.waitingTime = node.waitingTime;
        this.direction = node.direction;
        this.branchDirection = node.branchDirection;
    }

    public int getNodeNo() {
        return node.getNodeNo();
    }

    public String getRailOprIsttCd() {
        return node.getRailOprIsttCd();
    }

    public String getLnCd() {
        return node.getLnCd();
    }

    public String getStinCd() {
        return node.getStinCd();
    }

    public String getStinNm() {
        return node.getStinNm();
    }

    public int getUpDownOrder() {
        return node.getUpDownOrder();
    }

    public String getBranchInfo() {
        return node.getBranchInfo();
    }

    public boolean isJctStin() {
        return node.getIsJctStin() > 0;
    }

    public boolean isExpStin() {
        return node.getIsExpStin() == 1;
    }

    // 가중치를 기준으로 우선순위 결정
    @Override
    public int compareTo(MetroNodeWithEdge o) {
        return Double.compare(this.weight, o.weight);
    }

    @Override
    public String toString() {
        String nodeInfo = getLnCd() + ", " + getStinNm() + ", " + weight + ", " + waitingTime + ", " + direction + ", " + branchDirection;

        return getString(nodeInfo);
    }

    private String getString(String nodeInfo) {
        return switch (edgeType) {
            case EXP_EDGE -> "<<" + nodeInfo + ">>";
            case TRF_EDGE -> "[[" + nodeInfo + "]]";
            default -> '(' + nodeInfo + ')';
        };
    }
}

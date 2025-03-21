package kuraeyong.backend.domain.path;

import kuraeyong.backend.domain.constant.BranchDirectionType;
import kuraeyong.backend.domain.constant.DirectionType;
import kuraeyong.backend.domain.constant.EdgeType;
import kuraeyong.backend.domain.graph.MetroNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Setter
    private String passingTime; // for congestion

    public MetroNodeWithEdge(MetroNodeWithEdge node) {
        this.node = new MetroNode(node.node);
        this.weight = node.weight;
        this.edgeType = node.edgeType;
        this.waitingTime = node.waitingTime;
        this.direction = node.direction;
        this.branchDirection = node.branchDirection;
        this.passingTime = node.passingTime;
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
        return getJctStin() > 0;
    }

    public int getJctStin() {
        return node.getIsJctStin();
    }

    public boolean isExpStin() {
        return node.getIsExpStin() == 1;
    }

    public boolean isDifferentLine(String lnCd) {
        return !getLnCd().equals(lnCd);
    }

    public boolean isSameDirection(DirectionType directionType) {
        return getDirection().equals(directionType);
    }

    // 가중치를 기준으로 우선순위 결정
    @Override
    public int compareTo(MetroNodeWithEdge o) {
        return Double.compare(this.weight, o.weight);
    }

    @Override
    public String toString() {
        String nodeInfo = getLnCd() + ", " + getStinNm() + ", " + weight + ", " + waitingTime + ", " + direction + ", " + branchDirection + ", " + passingTime;

        return switch (edgeType) {
            case EXP_EDGE -> "<<" + nodeInfo + ">>";
            case TRF_EDGE -> "[[" + nodeInfo + "]]";
            default -> '(' + nodeInfo + ')';
        };
    }
}

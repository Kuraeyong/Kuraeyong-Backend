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

    public MetroNodeWithEdge(MetroNodeWithEdge node) {
        this.node = new MetroNode(node.node);
        this.weight = node.weight;
        this.edgeType = node.edgeType;
        this.waitingTime = node.waitingTime;
        this.direction = node.direction;
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
        return switch (edgeType) {
            case EXP_EDGE ->
                    "<<" + getLnCd() + ", " + getStinNm() + ", " + weight + ", " + waitingTime + ", " + direction + ">>";
            case TRF_EDGE ->
                    "[[" + getLnCd() + ", " + getStinNm() + ", " + weight + ", " + waitingTime + ", " + direction + "]]";
            default ->
                    '(' + getLnCd() + ", " + getStinNm() + ", " + weight + ", " + waitingTime + ", " + direction + ')';
        };
    }
}

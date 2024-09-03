package kuraeyong.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MetroNodeWithWeight implements Comparable<MetroNodeWithWeight> {
    private MetroNode node;
    @Setter
    private double weight;

    public MetroNodeWithWeight(MetroNodeWithWeight node) {
        this.node = new MetroNode(node.node);
        this.weight = node.weight;
    }

    public int getNodeNo() {
        return node.getNodeNo();
    }

    public String getStinNm() {
        return node.getStinNm();
    }

    public String getStinCd() {
        return node.getStinCd();
    }

    public String getLnCd() {
        return node.getLnCd();
    }

    public String getRailOprIsttCd() {
        return node.getRailOprIsttCd();
    }

    public boolean isJctStin() {
        return node.getIsJctStin() > 0;
    }

    // 가중치를 기준으로 우선순위 결정
    @Override
    public int compareTo(MetroNodeWithWeight o) {
        return Double.compare(this.weight, o.weight);
    }

    @Override
    public String toString() {
        return '(' + getLnCd() + ", " + getStinNm() + ", " + weight + ')';
    }
}

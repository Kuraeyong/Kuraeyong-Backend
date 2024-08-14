package kuraeyong.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MetroNode {
    private List<MetroEdge> edgeList;
    private String railOprIsttCd;
    private String lnCd;
    private String stinCd;
    private String stinNm;
    private int nodeNo;

    public void addEdge(MetroEdge edge) {
        edgeList.add(edge);
    }

    public int getWeightToDestNode(int destNodeNo) {
        for (MetroEdge edge : edgeList) {
            if (edge.getTrfNodeNo() == destNodeNo) {
                return edge.getWeight();
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return "MetroNode{" +
                "nodeNo=" + nodeNo +
                ", railOprIsttCd='" + railOprIsttCd + '\'' +
                ", lnCd='" + lnCd + '\'' +
                ", stinCd='" + stinCd + '\'' +
                ", stinNm='" + stinNm + '\'' +
                ", edgeList=" + edgeList +
                '}';
    }
}

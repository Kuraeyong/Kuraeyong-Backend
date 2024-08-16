package kuraeyong.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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

    public void printEdgeList() {
        for (MetroEdge edge : edgeList) {
            System.out.println(edge);
        }
    }

    public MetroNode(MetroNode node) {
        this.edgeList = new ArrayList<>();
        for (MetroEdge edge : node.getEdgeList()) {
            this.edgeList.add(new MetroEdge(edge));
        }
        this.railOprIsttCd = node.railOprIsttCd;
        this.lnCd = node.lnCd;
        this.stinCd = node.stinCd;
        this.stinNm = node.stinNm;
        this.nodeNo = node.nodeNo;
    }
}

package kuraeyong.backend.domain.graph;

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
    private int isJctStin;
    private int isExpStin;
    private int upDownOrder;
    private String branchInfo;

    public void addEdge(MetroEdge edge) {
        edgeList.add(edge);
    }

    @Override
    public String toString() {
        return "MetroNode{" +
                "railOprIsttCd='" + railOprIsttCd + '\'' +
                ", lnCd='" + lnCd + '\'' +
                ", stinCd='" + stinCd + '\'' +
                ", stinNm='" + stinNm + '\'' +
                ", nodeNo=" + nodeNo +
                ", isJctStin=" + isJctStin +
                ", isExpStin=" + isExpStin +
                ", upDownOrder=" + upDownOrder +
                ", branchInfo='" + branchInfo + '\'' +
                ", edgeList=" + edgeList +
                '}';
    }

    public MetroNode(MetroNode node) {
        this.edgeList = new ArrayList<>();
        for (MetroEdge edge : node.getEdgeList()) {
            addEdge(new MetroEdge(edge));
        }
        this.railOprIsttCd = node.railOprIsttCd;
        this.lnCd = node.lnCd;
        this.stinCd = node.stinCd;
        this.stinNm = node.stinNm;
        this.nodeNo = node.nodeNo;
        this.isJctStin = node.isJctStin;
        this.isExpStin = node.isExpStin;
        this.upDownOrder = node.upDownOrder;
        this.branchInfo = node.branchInfo;
    }
}

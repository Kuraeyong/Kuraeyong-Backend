package kuraeyong.backend.domain.graph;

import kuraeyong.backend.domain.constant.EdgeType;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MetroEdge {
    private String trfRailOprIsttCd;
    private String trfLnCd;
    private String trfStinCd;
    private String trfStinNm;
    private double weight;
    @Setter
    private int trfNodeNo;
    private EdgeType edgeType;

    @Override
    public String toString() {
        return "MetroEdge{" +
                "trfRailOprIsttCd='" + trfRailOprIsttCd + '\'' +
                ", trflnCd='" + trfLnCd + '\'' +
                ", trfStinCd='" + trfStinCd + '\'' +
                ", trfStinNm='" + trfStinNm + '\'' +
                ", weight=" + weight +
                ", trfNodeNo=" + trfNodeNo +
                ", edgeType=" + edgeType +
                '}';
    }

    public MetroEdge(MetroEdge edge) {
        this.trfRailOprIsttCd = edge.trfRailOprIsttCd;
        this.trfLnCd = edge.trfLnCd;
        this.trfStinCd = edge.trfStinCd;
        this.trfStinNm = edge.trfStinNm;
        this.weight = edge.weight;
        this.trfNodeNo = edge.trfNodeNo;
        this.edgeType = edge.edgeType;
    }

    public boolean isExpEdge() {
        return edgeType == EdgeType.EXP_EDGE;
    }
}

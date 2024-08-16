package kuraeyong.backend.domain;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MetroEdge {
    private String trfRailOprIsttCd;
    private String trflnCd;
    private String trfStinCd;
    private String trfStinNm;
    private double weight;
    @Setter
    private int trfNodeNo;

    @Override
    public String toString() {
        return "MetroEdge{" +
                "trfRailOprIsttCd='" + trfRailOprIsttCd + '\'' +
                ", trflnCd='" + trflnCd + '\'' +
                ", trfStinCd='" + trfStinCd + '\'' +
                ", trfStinNm='" + trfStinNm + '\'' +
                ", weight=" + weight +
                ", trfNodeNo=" + trfNodeNo +
                '}';
    }

    public MetroEdge(MetroEdge edge) {
        this.trfRailOprIsttCd = edge.trfRailOprIsttCd;
        this.trflnCd = edge.trflnCd;
        this.trfStinCd = edge.trfStinCd;
        this.trfStinNm = edge.trfStinNm;
        this.weight = edge.weight;
        this.trfNodeNo = edge.trfNodeNo;
    }
}

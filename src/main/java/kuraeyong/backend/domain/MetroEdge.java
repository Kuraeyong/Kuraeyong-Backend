package kuraeyong.backend.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class MetroEdge {
    private String trfRailOprIsttCd;
    private String trflnCd;
    private String trfStinCd;
    private String trfStinNm;
    private int weight;

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
}

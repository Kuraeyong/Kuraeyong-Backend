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
                ", edgeList=" + edgeList + '\'' +
                '}';
    }
}

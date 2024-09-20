package kuraeyong.backend.dto;

import kuraeyong.backend.domain.MetroNodeWithEdge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MinimumStationInfo {
    private String railOprIsttCd;
    private String lnCd;
    private String stinCd;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MinimumStationInfo that = (MinimumStationInfo) o;
        return Objects.equals(railOprIsttCd, that.railOprIsttCd) && Objects.equals(lnCd, that.lnCd) && Objects.equals(stinCd, that.stinCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(railOprIsttCd, lnCd, stinCd);
    }

    public static MinimumStationInfo build(String railOprIsttCd, String lnCd, String stinCd) {
        return MinimumStationInfo.builder()
                .railOprIsttCd(railOprIsttCd)
                .lnCd(lnCd)
                .stinCd(stinCd)
                .build();
    }

    public static MinimumStationInfo get(MetroNodeWithEdge node) {
        return build(node.getRailOprIsttCd(), node.getLnCd(), node.getStinCd());
    }
}

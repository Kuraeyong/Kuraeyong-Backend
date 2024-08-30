package kuraeyong.backend.dto;

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
}

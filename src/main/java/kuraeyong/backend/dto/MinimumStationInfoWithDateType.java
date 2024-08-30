package kuraeyong.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class MinimumStationInfoWithDateType {
    private MinimumStationInfo minimumStationInfo;
    private String stinCd;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MinimumStationInfoWithDateType that = (MinimumStationInfoWithDateType) o;
        return Objects.equals(minimumStationInfo, that.minimumStationInfo) && Objects.equals(stinCd, that.stinCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minimumStationInfo, stinCd);
    }
}

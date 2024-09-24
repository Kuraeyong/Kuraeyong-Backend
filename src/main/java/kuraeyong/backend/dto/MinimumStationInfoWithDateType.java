package kuraeyong.backend.dto;

import kuraeyong.backend.domain.MetroNodeWithEdge;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class MinimumStationInfoWithDateType {
    private MinimumStationInfo minimumStationInfo;
    private String dateType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MinimumStationInfoWithDateType that = (MinimumStationInfoWithDateType) o;
        return Objects.equals(minimumStationInfo, that.minimumStationInfo) && Objects.equals(dateType, that.dateType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minimumStationInfo, dateType);
    }

    public static MinimumStationInfoWithDateType get(MetroNodeWithEdge node, String dateType) {
        return new MinimumStationInfoWithDateType(MinimumStationInfo.get(node), dateType);
    }
}

package kuraeyong.backend.dto;

import kuraeyong.backend.domain.MetroNodeWithEdge;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
public class MinimumStationInfoWithDateType {
    private final MinimumStationInfo minimumStationInfo;
    private final String dateType;

    public MinimumStationInfoWithDateType(MinimumStationInfo minimumStationInfo, String dateType) {
        this.minimumStationInfo = minimumStationInfo;

        String lnCd = minimumStationInfo.getLnCd();
        if (!dateType.equals("토")) {
            this.dateType = dateType;
            return;
        }
        if (lnCd.equals("E1") || lnCd.equals("UI") || lnCd.equals("U1")) {
            this.dateType = dateType;
            return;
        }
        this.dateType = "휴일";
    }

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

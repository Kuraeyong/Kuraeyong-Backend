package kuraeyong.backend.domain.station.info;

import kuraeyong.backend.domain.constant.DomainType;
import kuraeyong.backend.domain.path.MetroNodeWithEdge;
import lombok.Getter;

import java.util.Objects;

@Getter
public class MinimumStationInfoWithDateType {
    private final MinimumStationInfo minimumStationInfo;
    private final String dateType;

    public MinimumStationInfoWithDateType(MinimumStationInfo minimumStationInfo, String dateType, DomainType domainType) {
        this.minimumStationInfo = minimumStationInfo;

        if (domainType == DomainType.STATION_TIME_TABLE) {
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
            return;
        }
        this.dateType = dateType;
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

    public static MinimumStationInfoWithDateType get(MetroNodeWithEdge node, String dateType, DomainType domainType) {
        return new MinimumStationInfoWithDateType(MinimumStationInfo.get(node), dateType, domainType);
    }
}

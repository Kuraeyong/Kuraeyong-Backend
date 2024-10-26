package kuraeyong.backend.domain.station.congestion;

import kuraeyong.backend.domain.constant.DomainType;
import kuraeyong.backend.domain.station.info.MinimumStationInfo;
import kuraeyong.backend.domain.station.info.MinimumStationInfoWithDateType;
import kuraeyong.backend.repository.StationCongestionRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class StationCongestionMap {
    private final HashMap<MinimumStationInfoWithDateType, StationCongestionList> map;

    public StationCongestionMap(StationCongestionRepository stationCongestionRepository) {
        map = new HashMap<>();

        for (StationCongestion row : stationCongestionRepository.findAll()) {
            MinimumStationInfo MSI = MinimumStationInfo.build(row.getRailOprIsttCd(), row.getLnCd(), row.getStinCd());
            MinimumStationInfoWithDateType key = new MinimumStationInfoWithDateType(MSI, row.getDayNm(), DomainType.STATION_CONGESTION);

            if (!map.containsKey(key)) {
                map.put(key, new StationCongestionList());
            }
            map.get(key).add(row);
        }
    }
}

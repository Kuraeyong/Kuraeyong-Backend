package kuraeyong.backend.domain;

import kuraeyong.backend.dto.MinimumStationInfo;
import kuraeyong.backend.dto.MinimumStationInfoWithDateType;
import kuraeyong.backend.repository.StationTimeTableElementRepository;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Getter
public class StationTimeTableMap {
    private final HashMap<MinimumStationInfoWithDateType, StationTimeTable> map;

    public StationTimeTableMap(StationTimeTableElementRepository stationTimeTableElementRepository) {
        map = new HashMap<>();

        for (StationTimeTableElement row : stationTimeTableElementRepository.findAll()) {
            MinimumStationInfo minimumStationInfo = MinimumStationInfo.builder()
                    .railOprIsttCd(row.getRailOprIsttCd())
                    .lnCd(row.getLnCd())
                    .stinCd(row.getStinCd())
                    .build();
            MinimumStationInfoWithDateType key = new MinimumStationInfoWithDateType(minimumStationInfo, row.getDayNm());

            if (!map.containsKey(key)) {
                map.put(key, new StationTimeTable());
            }
            map.get(key).add(row);
        }
    }
}

package kuraeyong.backend.domain;

import kuraeyong.backend.dto.MinimumStationInfo;
import kuraeyong.backend.dto.MinimumStationInfoWithDateType;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Getter
public class StationTimeTableMap {
    private final HashMap<MinimumStationInfoWithDateType, StationTimeTable> map;

    public StationTimeTableMap(List<StationTimeTableElement> stationTimeTableElementList) {
        map = new HashMap<>();

        for (StationTimeTableElement row : stationTimeTableElementList) {
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

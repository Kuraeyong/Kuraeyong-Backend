package kuraeyong.backend.domain;

import kuraeyong.backend.dto.MinimumStationInfo;
import kuraeyong.backend.dto.MinimumStationInfoWithDateType;
import kuraeyong.backend.repository.StationTimeTableElementRepository;
import kuraeyong.backend.repository.StationTrfWeightRepository;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Getter
public class StationTrfWeightMap {
    private final HashMap<MinimumStationInfo, StationTrfWeightList> map;

    public StationTrfWeightMap(StationTrfWeightRepository stationTrfWeightRepository) {
        map = new HashMap<>();

        for (StationTrfWeight row : stationTrfWeightRepository.findAll()) {
            MinimumStationInfo key = MinimumStationInfo.build(row.getRailOprIsttCd(), row.getLnCd(), row.getStinCd());

            if (!map.containsKey(key)) {
                map.put(key, new StationTrfWeightList());
            }
            map.get(key).add(row);
        }
    }

    public StationTrfWeightList get(MinimumStationInfo key) {
        return map.get(key);
    }

    public int getStationTrfWeight(MinimumStationInfo org, MinimumStationInfo dest, DirectionType dir) {
        for (StationTrfWeight row : get(org).getList()) {
            if (!dest.getLnCd().equals(row.getTrfLnCd())) {
                continue;
            }
            return switch (dir) {
                case UP_UP -> row.getUpUp();
                case UP_DOWN -> row.getUpDown();
                case DOWN_UP -> row.getDownUp();
                case DOWN_DOWN -> row.getDownDown();
                default -> -413;
            };
        }
        return -66;
    }
}

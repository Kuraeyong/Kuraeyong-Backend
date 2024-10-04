package kuraeyong.backend.domain.station.trf_weight;

import kuraeyong.backend.domain.constant.BranchDirectionType;
import kuraeyong.backend.domain.constant.DirectionType;
import kuraeyong.backend.domain.station.info.MinimumStationInfo;
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

    public int getStationTrfWeight(MinimumStationInfo org, MinimumStationInfo dest, BranchDirectionType branchDir, DirectionType dir) {
        for (StationTrfWeight row : get(org).getList()) {
            if (!dest.getLnCd().equals(row.getTrfLnCd())) { // 환승하고자 하는 노선이 아닌 경우
                continue;
            }
            if (branchDir != null) {
                if (!branchDir.get().equals(row.getTrfType())) {
                    continue;
                }
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

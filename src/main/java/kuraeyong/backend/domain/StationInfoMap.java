package kuraeyong.backend.domain;

import kuraeyong.backend.repository.StationInfoRepository;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Getter
public class StationInfoMap {
    private final HashMap<MinimumStationInfo, StationInfo> map;

    public StationInfoMap(StationInfoRepository stationInfoRepository) {
        map = new HashMap<>();

        for (StationInfo row : stationInfoRepository.findAll()) {
            MinimumStationInfo key = MinimumStationInfo.builder()
                    .railOprIsttCd(row.getRailOprIsttCd())
                    .lnCd(row.getLnCd())
                    .stinCd(row.getStinCd())
                    .build();

            map.put(key, row);
        }
    }

    public StationInfo get(MinimumStationInfo key) {
        return map.get(key);
    }

    public int getUpDownOrder(MinimumStationInfo key) {
        return get(key).getUpDownOrder();
    }

    public String getBranchInfo(MinimumStationInfo key) {
        return get(key).getBranchInfo();
    }
}

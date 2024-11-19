package kuraeyong.backend.domain.station.info;

import kuraeyong.backend.repository.StationInfoRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;

@Component
public class StationInfoMap {
    private final HashMap<MinimumStationInfo, StationInfo> map;
    private final HashSet<String> railOprIsttCds;

    public StationInfoMap(StationInfoRepository stationInfoRepository) {
        map = new HashMap<>();
        railOprIsttCds = new HashSet<>();

        for (StationInfo row : stationInfoRepository.findAll()) {
            MinimumStationInfo key = MinimumStationInfo.builder()
                    .railOprIsttCd(row.getRailOprIsttCd())
                    .lnCd(row.getLnCd())
                    .stinCd(row.getStinCd())
                    .build();

            map.put(key, row);
            railOprIsttCds.add(row.getRailOprIsttCd());
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

    public String getStinNm(MinimumStationInfo key) {
        StationInfo stationInfo = get(key);
        if (stationInfo == null) {
            return null;
        }
        return get(key).getStinNm();
    }

    public MinimumStationInfo createValidMSI(String defaultRailOprIsttCd, String lnCd, String stinCd) {
        MinimumStationInfo key = MinimumStationInfo.build(defaultRailOprIsttCd, lnCd, stinCd);
        if (get(key) != null) {
            return key;
        }

        for (String railOprIsttCd : railOprIsttCds) {
            key = MinimumStationInfo.build(railOprIsttCd, lnCd, stinCd);
            if (get(key) != null) {
                return key;
            }
        }
        return null;
    }
}

package kuraeyong.backend.domain.station.congestion;

import kuraeyong.backend.domain.constant.DomainType;
import kuraeyong.backend.domain.path.MetroNodeWithEdge;
import kuraeyong.backend.domain.path.PathResult;
import kuraeyong.backend.domain.path.PathResultList;
import kuraeyong.backend.domain.station.info.MinimumStationInfo;
import kuraeyong.backend.domain.station.info.MinimumStationInfoWithDateType;
import kuraeyong.backend.repository.StationCongestionRepository;
import kuraeyong.backend.util.DateUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Set;

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

        // TODO. 방향을 기준으로 정렬 (상행이 앞으로)
        Set<MinimumStationInfoWithDateType> keySet = map.keySet();
        for (MinimumStationInfoWithDateType key : keySet) {
            map.get(key).sort();
        }
    }

    public void calculateCongestionOfPathes(PathResultList pathResultList, String dateType) {
        for (PathResult pathResult : pathResultList.getList()) {
            calculateCongestionOfPath(pathResult, dateType);
        }
    }

    private void calculateCongestionOfPath(PathResult pathResult, String dateType) {
        double sum = 0;
        double maxCongestion = -1;
        int cnt = 0;
        for (MetroNodeWithEdge node : pathResult.getMetroNodeWithEdgeList()) {
            MinimumStationInfo MSI = MinimumStationInfo.get(node);
            MinimumStationInfoWithDateType key = new MinimumStationInfoWithDateType(MSI, dateType, DomainType.STATION_CONGESTION);
            StationCongestionList stationCongestionList = map.get(key);
            if (stationCongestionList == null) {
                pathResult.setValidCongestion(false);
                continue;
            }
            String time = DateUtil.passingTimeToCongestionTime(node.getPassingTime());
            double congestion = stationCongestionList.get(node.getDirection()).getTime(time);
            if (congestion == -1) {
                pathResult.setValidCongestion(false);
                continue;
            }
            maxCongestion = Math.max(congestion, maxCongestion);
            sum += congestion;
            cnt++;
        }
        if (cnt == 0) {
            pathResult.setAverageCongestion(-1);
        }
        pathResult.setAverageCongestion((int) sum / cnt);
        pathResult.setMaxCongestion((int) maxCongestion);
    }
}

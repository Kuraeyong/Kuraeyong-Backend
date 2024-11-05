package kuraeyong.backend.domain.station.congestion;

import kuraeyong.backend.domain.constant.DomainType;
import kuraeyong.backend.domain.constant.EdgeType;
import kuraeyong.backend.domain.path.MetroNodeWithEdge;
import kuraeyong.backend.domain.path.PathResult;
import kuraeyong.backend.domain.path.PathResults;
import kuraeyong.backend.domain.station.info.MinimumStationInfo;
import kuraeyong.backend.domain.station.info.MinimumStationInfoWithDateType;
import kuraeyong.backend.repository.StationCongestionRepository;
import kuraeyong.backend.util.DateUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
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

        // TODO. 방향을 기준으로 정렬 (하행이 앞으로)
        Set<MinimumStationInfoWithDateType> keySet = map.keySet();
        for (MinimumStationInfoWithDateType key : keySet) {
            map.get(key).sort();
        }
    }

    public void setCongestionScoreOfPaths(PathResults pathResults, String dateType, int congestionThreshold) {
        for (PathResult pathResult : pathResults.getList()) {
            setCongestionScoreOfPath(pathResult, dateType, congestionThreshold);
        }
    }

    private void setCongestionScoreOfPath(PathResult pathResult, String dateType, int congestionThreshold) {
        final int CONGESTION_PENALTY = 10000;
        int congestionScore = 0;

        List<MetroNodeWithEdge> path = pathResult.getMetroNodeWithEdgeList();
        for (int i = 0; i < path.size(); i++) {
            // 해당 요일 종류에 혼잡도 정보를 제공하는 역인지 검사
            MetroNodeWithEdge curr = path.get(i);
            MinimumStationInfo MSI = MinimumStationInfo.get(curr);
            MinimumStationInfoWithDateType key = new MinimumStationInfoWithDateType(MSI, dateType, DomainType.STATION_CONGESTION);
            StationCongestionList stationCongestionList = map.get(key);
            if (stationCongestionList == null) {
                pathResult.setCongestionScore(Integer.MAX_VALUE);
                return;
            }

            // 해당 시간대에 혼잡도 정보를 제공하는지 검사
            String time = DateUtil.passingTimeToCongestionTime(curr.getPassingTime());
            double congestion = stationCongestionList.get(curr.getDirection()).getTime(time);
            if (congestion == -1) {
                pathResult.setCongestionScore(Integer.MAX_VALUE);
                return;
            }

            // 혼잡도 점수 계산
            if (congestion <= congestionThreshold) {
                continue;
            }
            MetroNodeWithEdge next = (i == path.size() - 1) ? null : path.get(i + 1);
            if (isPenaltyStation(curr, next, i)) {
                congestionScore += CONGESTION_PENALTY;
                continue;
            }
            congestionScore++;
        }
        pathResult.setCongestionScore(congestionScore);
    }

    /**
     * 혼잡도 점수에 페널티를 부과해야 하는 역인지 검사
     *
     * @param curr 현재역
     * @param next 다음역
     * @param idx  현재역의 인덱스
     * @return 페널티 부과 역 여부
     */
    private boolean isPenaltyStation(MetroNodeWithEdge curr, MetroNodeWithEdge next, int idx) {
        if (idx == 0) { // 출발역
            return true;
        }
        if (next == null) { // 도착역
            return true;
        }
        return (curr.getEdgeType() == EdgeType.TRF_EDGE) || (next.getEdgeType() == EdgeType.TRF_EDGE);  // 환승역
    }
}

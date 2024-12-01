package kuraeyong.backend.domain.station.congestion;

import kuraeyong.backend.domain.constant.DomainType;
import kuraeyong.backend.domain.constant.EdgeType;
import kuraeyong.backend.domain.path.ActualPath;
import kuraeyong.backend.domain.path.ActualPaths;
import kuraeyong.backend.domain.path.MetroNodeWithEdge;
import kuraeyong.backend.domain.station.info.MinimumStationInfo;
import kuraeyong.backend.domain.station.info.MinimumStationInfoWithDateType;
import kuraeyong.backend.manager.station.StationCongestionManager;
import kuraeyong.backend.util.DateUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Component
public class StationCongestionMap {
    private final HashMap<MinimumStationInfoWithDateType, StationCongestionList> map;

    public StationCongestionMap(StationCongestionManager stationCongestionManager) {
        map = new HashMap<>();

        for (StationCongestion row : stationCongestionManager.findAll()) {
            MinimumStationInfo MSI = MinimumStationInfo.build(row.getRailOprIsttCd(), row.getLnCd(), row.getStinCd());
            MinimumStationInfoWithDateType key = new MinimumStationInfoWithDateType(MSI, row.getDayNm(), DomainType.STATION_CONGESTION);

            if (!map.containsKey(key)) {
                map.put(key, new StationCongestionList());
            }
            map.get(key).add(row);
        }

        // 방향을 기준으로 정렬 (하행이 앞으로)
        Set<MinimumStationInfoWithDateType> keySet = map.keySet();
        for (MinimumStationInfoWithDateType key : keySet) {
            map.get(key).sort();
        }
    }

    public void setCongestionScoreOfPaths(ActualPaths actualPaths, String dateType, int congestionThreshold) {
        for (ActualPath actualPath : actualPaths.getList()) {
            setCongestionScoreOfPath(actualPath, dateType, congestionThreshold);
        }
    }

    private void setCongestionScoreOfPath(ActualPath actualPath, String dateType, int congestionThreshold) {
        final int CONGESTION_PENALTY = 10000;
        final int UNKNOWN_CONGESTION = 1000000;
        int congestionScore = 0;
        int congestionCount = 0;
        double totalCongestion = 0;
        double maxCongestion = -1;

        List<MetroNodeWithEdge> path = actualPath.getIterablePath();
        for (int i = 0; i < path.size(); i++) {
            // 해당 요일 종류에 혼잡도 정보를 제공하는 역인지 검사
            MetroNodeWithEdge curr = path.get(i);
            MinimumStationInfo MSI = MinimumStationInfo.get(curr);
            MinimumStationInfoWithDateType key = new MinimumStationInfoWithDateType(MSI, dateType, DomainType.STATION_CONGESTION);
            StationCongestionList stationCongestionList = map.get(key);
            if (stationCongestionList == null) {
                actualPath.setCongestionScore(UNKNOWN_CONGESTION);
                return;
            }

            // 해당 시간대에 혼잡도 정보를 제공하는지 검사
            String time = DateUtil.passingTimeToCongestionTime(curr.getPassingTime());
            double congestion = stationCongestionList.get(curr.getDirection()).getTime(time);
            if (congestion == -1) {
                actualPath.setCongestionScore(UNKNOWN_CONGESTION);
                return;
            }

            // 최대, 평균 혼잡도 계산
            congestionCount++;
            totalCongestion += congestion;
            maxCongestion = Math.max(maxCongestion, congestion);

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
        actualPath.setCongestionScore(congestionScore);
        actualPath.setAvgCongestion(congestionCount == 0 ? -1 : (int) totalCongestion / congestionCount);
        actualPath.setMaxCongestion((int) maxCongestion);
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

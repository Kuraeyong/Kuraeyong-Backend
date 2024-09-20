package kuraeyong.backend.domain;

import kuraeyong.backend.dto.MinimumStationInfo;
import kuraeyong.backend.dto.MinimumStationInfoWithDateType;
import kuraeyong.backend.dto.MoveInfo;
import kuraeyong.backend.repository.StationTimeTableElementRepository;
import kuraeyong.backend.util.DateUtil;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Component
public class StationTimeTableMap {
    private final HashMap<MinimumStationInfoWithDateType, StationTimeTable> map;

    public StationTimeTableMap(StationTimeTableElementRepository stationTimeTableElementRepository) {
        map = new HashMap<>();

        for (StationTimeTableElement row : stationTimeTableElementRepository.findAll()) {
            if (row.getDptTm().equals("null") && row.getArvTm().equals("null")) {
                continue;
            }
            MinimumStationInfo minimumStationInfo = MinimumStationInfo.build(row.getRailOprIsttCd(), row.getLnCd(), row.getStinCd());
            MinimumStationInfoWithDateType key = new MinimumStationInfoWithDateType(minimumStationInfo, row.getDayNm());

            if (!map.containsKey(key)) {
                map.put(key, new StationTimeTable());
            }
            map.get(key).add(row);
        }

        // 출발 시간을 기준으로 정렬
        Set<MinimumStationInfoWithDateType> keySet = map.keySet();
        for (MinimumStationInfoWithDateType key : keySet) {
            map.get(key).sort();
        }
    }

    public StationTimeTable get(MinimumStationInfoWithDateType key) {
        return map.get(key);
    }

    /**
     * @param stin  고유한 역 정보
     * @return  특정역에서의 평균 배차시간을 반환
     */
    public double getAvgWaitingTime(MinimumStationInfoWithDateType stin) {
        if (!map.containsKey(stin)) {
            return -1;
        }
        StationTimeTable trainList = map.get(stin);
        StationTimeTableElement firstTrain = trainList.get(0);
        StationTimeTableElement lastTrain = trainList.get(trainList.size() - 1);
        int firstTrainArvTm = DateUtil.getTimeForCompare(firstTrain.getArvTm(), firstTrain.getDptTm());
        int lastTrainArvTm = DateUtil.getTimeForCompare(lastTrain.getArvTm(), lastTrain.getDptTm());
        int totalDuration = DateUtil.timeToMinute(lastTrainArvTm - firstTrainArvTm);
        double avgWaitingTime = (double) totalDuration / (trainList.size() - 1);

        return Math.round(avgWaitingTime * 10) / 10.0;
    }
}

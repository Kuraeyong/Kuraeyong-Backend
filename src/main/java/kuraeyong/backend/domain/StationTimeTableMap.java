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
@Getter
public class StationTimeTableMap {
    private final HashMap<MinimumStationInfoWithDateType, StationTimeTable> map;
    private final static int TRAIN_CANDIDATE_CNT = 3;

    public StationTimeTableMap(StationTimeTableElementRepository stationTimeTableElementRepository) {
        map = new HashMap<>();

        for (StationTimeTableElement row : stationTimeTableElementRepository.findAll()) {
            if (row.getDptTm().equals("null") && row.getArvTm().equals("null")) {
                continue;
            }
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

        // 출발 시간을 기준으로 정렬
        Set<MinimumStationInfoWithDateType> keySet = map.keySet();
        for (MinimumStationInfoWithDateType key : keySet) {
            map.get(key).sort();
        }
    }

    /**
     * @param curr     현재 역 (A)
     * @param next     다음 역 (B)
     * @param dateType 날짜 유형 (평일 | 토요일 | 공휴일)
     * @param currTime 현재 시간
     * @return 이동 정보
     */
    public MoveInfo getMoveInfo(MetroNodeWithEdge curr, MetroNodeWithEdge next, String dateType, String currTime) {
        if (curr.getStinNm().equals(next.getStinNm())) {    // 환승역인 경우
            int weight = (int) next.getWeight();
            return MoveInfo.builder()
                    .lnCd(null)
                    .trnNo(null)
                    .dptTm(currTime)
                    .arvTm(DateUtil.plusMinutes(currTime, weight))
                    .build();
        }

        // 현재역과 다음역을 고유하게 식별
        MinimumStationInfo A = getMinimumStationInfo(curr);
        MinimumStationInfo B = getMinimumStationInfo(next);

        // 현재역과 다음역의 시간표
        StationTimeTable A_TimeTable = map.get(new MinimumStationInfoWithDateType(A, dateType));
        StationTimeTable B_TimeTable = map.get(new MinimumStationInfoWithDateType(B, dateType));

        // 현재 시간 이후에 A역에 오는 열차 리스트 (이후 상시 적용)
        List<StationTimeTableElement> A_TrainList = A_TimeTable.findByDptTmGreaterThan(currTime);
        if (A_TrainList == null) {
            return null;
        }

        int cnt = 0;
        StationTimeTableElement B_FastestTrain = null;   // A에서 B로 가장 빠르게 이동할 수 있는 열차 (B역 기준 시간표)
        StationTimeTableElement A_FastestTrain = null;   // A에서 B로 가장 빠르게 이동할 수 있는 열차 (A역 기준 시간표)
        for (StationTimeTableElement A_Train : A_TrainList) {
            StationTimeTableElement B_Train = B_TimeTable.getStoppingTrainAfterCurrTime(A_Train.getTrnNo(), A_Train.getDptTm());    // A에서 B로 이동할 수 있는 열차 중 하나 (B역 기준 시간표)
            if (B_Train == null) {  // 해당 열차가 B역에 정차하지 않는다면
                continue;
            }
            if (B_FastestTrain == null || B_Train.getArvTm().compareTo(B_FastestTrain.getArvTm()) <= 0) {
                B_FastestTrain = B_Train;
                A_FastestTrain = A_Train;
            }
            if (++cnt >= TRAIN_CANDIDATE_CNT) {
                break;
            }
        }

        assert A_FastestTrain != null;
        return MoveInfo.builder()
                .lnCd(A_FastestTrain.getLnCd())
                .trnNo(A_FastestTrain.getTrnNo())
                .dptTm(A_FastestTrain.getDptTm())
                .arvTm(B_FastestTrain.getArvTm())
                .build();
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

        return (double) totalDuration / (trainList.size() - 1);
    }

    private static MinimumStationInfo getMinimumStationInfo(MetroNodeWithEdge node) {
        return MinimumStationInfo.builder()
                .railOprIsttCd(node.getRailOprIsttCd())
                .lnCd(node.getLnCd())
                .stinCd(node.getStinCd())
                .build();
    }
}

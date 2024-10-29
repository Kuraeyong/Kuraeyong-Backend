package kuraeyong.backend.service;

import kuraeyong.backend.domain.constant.DomainType;
import kuraeyong.backend.domain.constant.EdgeType;
import kuraeyong.backend.domain.path.MetroNodeWithEdge;
import kuraeyong.backend.domain.path.MetroPath;
import kuraeyong.backend.domain.path.MoveInfo;
import kuraeyong.backend.domain.path.MoveInfoList;
import kuraeyong.backend.domain.station.info.MinimumStationInfo;
import kuraeyong.backend.domain.station.info.MinimumStationInfoWithDateType;
import kuraeyong.backend.domain.station.time_table.StationTimeTable;
import kuraeyong.backend.domain.station.time_table.StationTimeTableElement;
import kuraeyong.backend.domain.station.time_table.StationTimeTableMap;
import kuraeyong.backend.domain.station.trf_weight.StationTrfWeightMap;
import kuraeyong.backend.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoveService {
    private final StationTimeTableMap stationTimeTableMap;
    private final StationTrfWeightMap stationTrfWeightMap;
    private final static int TRAIN_CANDIDATE_CNT = 3;

    /**
     * @param compressedPath e.g.) (K4, 행신, 0.0) (K4, 홍대입구, 2.5) (2, 홍대입구, 6.5) (2, 성수, 5.5) (2, 용답, 3.5)
     * @param dateType       날짜 종류 (평일 | 토요일 | 공휴일)
     * @param hour           사용자의 해당 역 도착 시간 (시간)
     * @param min            사용자의 해당 역 도착 시간 (분)
     * @return 이동 정보 리스트
     */
    public MoveInfoList createMoveInfoList(MetroPath compressedPath, String dateType, int hour, int min) {
        MoveInfoList moveInfoList = new MoveInfoList();
        String currTime = DateUtil.getCurrTime(hour, min);

        moveInfoList.add(MoveInfo.builder()
                .arvTm(currTime)
                .build());
        for (int i = 0; i < compressedPath.size() - 1; i++) {
            MetroNodeWithEdge curr = compressedPath.get(i);
            MetroNodeWithEdge next = compressedPath.get(i + 1);

            MoveInfo moveInfo = createMoveInfo(curr, next, dateType, currTime);
            if (moveInfo == null) {
                return null;
            }
            currTime = moveInfo.getArvTm();
            moveInfoList.add(moveInfo);
        }
        compressedPath.get(compressedPath.size() - 1)
                .setPassingTime(moveInfoList.get(moveInfoList.size() - 1).getArvTm());  // 도착역에 최종 도착 시간을 기재
        removeUnnecessaryTrain(moveInfoList, compressedPath, dateType);
        setTrfInfo(moveInfoList, dateType);

        return moveInfoList;
    }

    /**
     * @param curr     현재 역 (A)
     * @param next     다음 역 (B)
     * @param dateType 날짜 유형 (평일 | 토요일 | 공휴일)
     * @param currTime 현재 시간
     * @return 이동 정보
     */
    private MoveInfo createMoveInfo(MetroNodeWithEdge curr, MetroNodeWithEdge next, String dateType, String currTime) {
        if (next.getEdgeType() == EdgeType.TRF_EDGE) {    // 환승역인 경우
            MinimumStationInfo currMSI = MinimumStationInfo.get(curr);
            MinimumStationInfo nextMSI = MinimumStationInfo.get(next);
            int weight = stationTrfWeightMap.getStationTrfWeight(currMSI, nextMSI, next.getBranchDirection(), next.getDirection());

            return MoveInfo.builder()
                    .lnCd(null)
                    .trnNo(null)
                    .dptTm(currTime)
                    .arvTm(DateUtil.plusMinutes(currTime, weight))
                    .build();
        }

        // 현재역과 다음역을 고유하게 식별
        MinimumStationInfo A_MSI = MinimumStationInfo.get(curr);
        MinimumStationInfo B_MSI = MinimumStationInfo.get(next);

        // 현재역과 다음역의 시간표
        StationTimeTable A_TimeTable = stationTimeTableMap.get(new MinimumStationInfoWithDateType(A_MSI, dateType, DomainType.STATION_TIME_TABLE));
        StationTimeTable B_TimeTable = stationTimeTableMap.get(new MinimumStationInfoWithDateType(B_MSI, dateType, DomainType.STATION_TIME_TABLE));
        if (A_TimeTable == null || B_TimeTable == null) {
            return null;
        }

        // 현재 시간 이후에 A역에 오는 열차 리스트 (이후 상시 적용)
        List<StationTimeTableElement> A_TrainList = A_TimeTable.findByDptTmGreaterThanEqual(currTime);
        if (A_TrainList == null) {
            return null;
        }

        int cnt = 0;
        StationTimeTableElement B_FastestTrain = null;   // A에서 B로 가장 빠르게 이동할 수 있는 열차 (B역 기준 시간표)
        StationTimeTableElement A_FastestTrain = null;   // A에서 B로 가장 빠르게 이동할 수 있는 열차 (A역 기준 시간표)
        for (StationTimeTableElement A_Train : A_TrainList) {
            if (A_Train.isTmnStin()) {    // A가 해당 열차의 종점인 경우
                continue;
            }
            StationTimeTableElement B_Train = B_TimeTable.getStoppingTrainAfterCurrTime(A_Train.getTrnNo(), A_Train.getDptTm());    // A에서 B로 이동할 수 있는 열차 중 하나 (B역 기준 시간표)
            if (B_Train == null) {  // 해당 열차가 B역에 정차하지 않는다면
                continue;
            }
            if (B_FastestTrain == null || B_Train.getArvTm().compareTo(B_FastestTrain.getArvTm()) <= 0) {   // B_FastestTrain 갱신이 필요한 경우
                B_FastestTrain = B_Train;
                A_FastestTrain = A_Train;
            }
            if (++cnt >= TRAIN_CANDIDATE_CNT) {
                break;
            }
        }
        if (A_FastestTrain == null) {
            return null;
        }

        curr.setPassingTime(A_FastestTrain.getDptTm());
        return MoveInfo.builder()
                .lnCd(A_FastestTrain.getLnCd())
                .trnNo(A_FastestTrain.getTrnNo())
                .dptTm(A_FastestTrain.getDptTm())
                .arvTm(B_FastestTrain.getArvTm())
                .build();
    }

    /**
     * 동일 노선 내에서의 불필요한 환승 제거
     */
    private void removeUnnecessaryTrain(MoveInfoList moveInfoList, MetroPath compressedPath, String dateType) {
        for (int i = moveInfoList.size() - 2; i >= 1; i--) {
            MoveInfo TO_A = moveInfoList.get(i - 1);
            MoveInfo TO_B = moveInfoList.get(i);
            MoveInfo TO_C = moveInfoList.get(i + 1);
            if (TO_B.getTrnNo() == null || TO_C.getTrnNo() == null) {
                continue;
            }
            if (!TO_B.getLnCd().equals(TO_C.getLnCd())) {
                continue;
            }
            if (stationTimeTableMap.isSameTrain(TO_B.getTrnNo(), TO_C.getTrnNo(), TO_B.getLnCd(), dateType)) {
                continue;
            }
            MinimumStationInfoWithDateType A_Key = MinimumStationInfoWithDateType.get(compressedPath.get(i - 1), dateType, DomainType.STATION_TIME_TABLE);
            StationTimeTableElement A_Train = stationTimeTableMap.getStoppingTrainAfterCurrTime(A_Key, TO_C.getTrnNo(), TO_A.getArvTm());
            if (A_Train == null) {
                continue;
            }
            MinimumStationInfoWithDateType B_Key = MinimumStationInfoWithDateType.get(compressedPath.get(i), dateType, DomainType.STATION_TIME_TABLE);
            StationTimeTableElement B_Train = stationTimeTableMap.getStoppingTrainAfterCurrTime(B_Key, TO_C.getTrnNo(), TO_B.getArvTm());

            // TODO. 불필요한 환승 제거
            TO_B.setTrnNo(TO_C.getTrnNo());
            TO_B.setDptTm(A_Train.getDptTm());
            TO_B.setArvTm(B_Train.getArvTm());
        }
    }

    /**
     * 해당 이동정보의 환승 관련 정보 설정
     */
    private void setTrfInfo(MoveInfoList moveInfoList, String dateType) {
        int trfCnt = 0;
        int totalTrfTime = 0;
        int trnGroupNo = 0;

        for (int i = 0; i < moveInfoList.size() - 1; i++) {
            MoveInfo curr = moveInfoList.get(i);
            MoveInfo next = moveInfoList.get(i + 1);
            String currTrnNo = curr.getTrnNo();
            String nextTrnNo = next.getTrnNo();

            if (currTrnNo == null) {
                curr.setTrnGroupNo(-1);
                continue;
            }
            if (nextTrnNo == null) {
                trfCnt++;
                totalTrfTime += DateUtil.getMinDiff(next.getDptTm(), next.getArvTm());
                curr.setTrnGroupNo(trnGroupNo++);
                continue;
            }
            if (stationTimeTableMap.isSameTrain(currTrnNo, nextTrnNo, curr.getLnCd(), dateType)) {
                curr.setTrnGroupNo(trnGroupNo);
                continue;
            }
            curr.setTrnGroupNo(trnGroupNo++);
            trfCnt++;
        }
        moveInfoList.get(moveInfoList.size() - 1).setTrnGroupNo(trnGroupNo);
        moveInfoList.setTrfCnt(trfCnt);
        moveInfoList.setTotalTrfTime(totalTrfTime);
    }
}

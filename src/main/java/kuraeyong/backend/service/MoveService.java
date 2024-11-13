package kuraeyong.backend.service;

import kuraeyong.backend.domain.constant.BranchDirectionType;
import kuraeyong.backend.domain.constant.DirectionType;
import kuraeyong.backend.domain.constant.DomainType;
import kuraeyong.backend.domain.constant.EdgeType;
import kuraeyong.backend.domain.path.MetroNodeWithEdge;
import kuraeyong.backend.domain.path.MetroPath;
import kuraeyong.backend.domain.path.MoveInfo;
import kuraeyong.backend.domain.path.MoveInfos;
import kuraeyong.backend.domain.path.PathResult;
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
    public MoveInfos createMoveInfos(MetroPath compressedPath, String dateType, int hour, int min, PathResult front) {
        MoveInfos moveInfos = new MoveInfos();
        String currTime = createCurrTime(hour, min, front, compressedPath);

        moveInfos.add(MoveInfo.builder()
                .dptTm(DateUtil.getCurrTime(hour, min))
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
            moveInfos.add(moveInfo);
        }
        compressedPath.get(compressedPath.size() - 1)
                .setPassingTime(moveInfos.get(moveInfos.size() - 1).getArvTm());  // 도착역에 최종 도착 시간을 기재
        removeUnnecessaryTrain(moveInfos, compressedPath, dateType);
        setTrfInfo(moveInfos, dateType);

        return moveInfos;
    }

    /**
     * 환승 경유를 하는 경로 탐색인 경우, currTime에 환승 시간을 더해줘야 함
     *
     * @param hour  현재 시간
     * @param min   현재 분
     * @param front 출발역부터 환승역까지의 경로 탐색 결과
     * @return 환승 시간을 고려한 currTime
     */
    private String createCurrTime(int hour, int min, PathResult front, MetroPath rearCompressedPath) {
        String currTime = DateUtil.getCurrTime(hour, min);
        if (front == null) {
            return currTime;
        }
        MetroPath frontCompressedPath = front.getCompressedPath();
        MetroNodeWithEdge frontBeforeLastNode = frontCompressedPath.getFromEnd(2);
        MetroNodeWithEdge frontLastNode = frontCompressedPath.getFromEnd(1);
        MetroNodeWithEdge rearFirstNode = rearCompressedPath.get(0);
        MetroNodeWithEdge rearSecondNode = rearCompressedPath.get(1);
        return updateCurrTime(currTime, frontBeforeLastNode, frontLastNode, rearFirstNode, rearSecondNode);
    }

    private String updateCurrTime(String currTime, MetroNodeWithEdge frontBeforeLastNode, MetroNodeWithEdge frontLastNode, MetroNodeWithEdge rearFirstNode, MetroNodeWithEdge rearSecondNode) {
        if (!frontLastNode.getLnCd().equals(rearFirstNode.getLnCd())) { // 노선 환승
            MinimumStationInfo org = MinimumStationInfo.get(frontLastNode);
            MinimumStationInfo dest = MinimumStationInfo.get(rearFirstNode);
            DirectionType directionType = DirectionType.convertToTrfDirectionType(frontLastNode.getDirection(), rearFirstNode.getDirection());
            int trfTime = stationTrfWeightMap.getStationTrfWeight(org, dest, null, directionType);
            return DateUtil.plusMinutes(currTime, trfTime);
        }
        if (BranchDirectionType.isBranchTrf(frontBeforeLastNode, frontLastNode, rearSecondNode)) {    // 분기점 환승 (본지 or 지본)
            int trfTime = rearFirstNode.getJctStin();
            return DateUtil.plusMinutes(currTime, trfTime);
        }
        if (!frontLastNode.getDirection().equals(rearFirstNode.getDirection())) {   // 유턴
            return DateUtil.plusMinutes(currTime, 1);
        }
        return currTime;
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
    private void removeUnnecessaryTrain(MoveInfos moveInfos, MetroPath compressedPath, String dateType) {
        for (int i = moveInfos.size() - 2; i >= 1; i--) {
            MoveInfo TO_A = moveInfos.get(i - 1);
            MoveInfo TO_B = moveInfos.get(i);
            MoveInfo TO_C = moveInfos.get(i + 1);
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
    private void setTrfInfo(MoveInfos moveInfos, String dateType) {
        int trfCnt = 0;
        int totalTrfTime = 0;
        int trnGroupNo = 0;

        for (int i = 0; i < moveInfos.size() - 1; i++) {
            MoveInfo curr = moveInfos.get(i);
            MoveInfo next = moveInfos.get(i + 1);
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
        moveInfos.get(moveInfos.size() - 1).setTrnGroupNo(trnGroupNo);
        moveInfos.setTrfCnt(trfCnt);
        moveInfos.setTotalTrfTime(totalTrfTime);
    }

    public MoveInfos join(MoveInfos front, MoveInfos rear, String dateType) {
        MoveInfos moveInfos = new MoveInfos(front);

        // 환승 경유인 경우 무브인포 추가
        MoveInfo moveInfo = rear.get(0);
        if (!moveInfo.getArvTm().equals(moveInfo.getDptTm())) {
            moveInfos.add(moveInfo);
        }

        // 남은 무브인포 연결
        for (int i = 1; i < rear.size(); i++) {
            moveInfos.add(new MoveInfo(rear.get(i)));
        }

        // 환승 정보 재설정
        setTrfInfo(moveInfos, dateType);

        return moveInfos;
    }
}

package kuraeyong.backend.manager.path;

import kuraeyong.backend.domain.constant.SortType;
import kuraeyong.backend.domain.path.ActualPath;
import kuraeyong.backend.domain.path.ActualPaths;
import kuraeyong.backend.domain.path.MetroPath;
import kuraeyong.backend.domain.path.MoveInfos;
import kuraeyong.backend.domain.station.congestion.StationCongestionMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ActualPathsManager {

    private final MoveInfosManager moveInfosManager;
    private final StationCongestionMap stationCongestionMap;

    /**
     * 임시 경로 목록과 시간 정보를 이용해 실제 경로 목록을 생성
     *
     * @param temporaryPaths 임시 경로 목록
     * @param dateType       요일 정보
     * @param hour           시간
     * @param min            분
     * @param front          특정 역을 경유하는 경로 탐색인 경우, 출발역에서 경유역까지의 실제 경로
     * @param stopoverTime   경유역에서 경유하는 시간
     * @return 실제 경로 목록
     */
    public ActualPaths create(List<MetroPath> temporaryPaths, String dateType, int hour, int min, ActualPath front, int stopoverTime) {
        ActualPaths actualPaths = new ActualPaths();
        for (MetroPath path : temporaryPaths) {
            MetroPath compressedPath = path.createCompressedPath();
            MoveInfos moveInfos = moveInfosManager.create(compressedPath, dateType, hour, min, front, stopoverTime);
            if (moveInfos == null) {
                continue;
            }
            actualPaths.add(new ActualPath(path, compressedPath, moveInfos));
        }
        return actualPaths;
    }

    /**
     * 두 실제 경로를 연결한, 새로운 실제 경로 반환
     *
     * @param front    출발역에서 경유역까지의 실제 경로
     * @param rear     경유역에서 도착역까지의 실제 경로
     * @param dateType 요일 종류
     * @return 연결된 실제 경로
     */
    public ActualPath join(ActualPath front, ActualPath rear, String dateType) {
        // 일반 경로 합치기
        MetroPath totalPath = new MetroPath(front.getPath());
        totalPath.concat(rear.getPath(), false);

        // 압축 경로 합치기
        MetroPath totalCompressedPath = new MetroPath(front.getCompressedPath());
        totalCompressedPath.concat(rear.getCompressedPath(), false);

        // 이동 정보 합치기
        MoveInfos totalMoveInfos = moveInfosManager.join(front.getMoveInfos(), rear.getMoveInfos(), dateType);

        // 혼잡도 점수 계산
        int congestionScore = (front.getCongestionScore() + rear.getCongestionScore()) / 2;
        int avgCongestion = isValidCongestion(front.getAvgCongestion(), rear.getAvgCongestion()) ?
                (front.getAvgCongestion() + rear.getAvgCongestion()) / 2 : -1;
        int maxCongestion = isValidCongestion(front.getAvgCongestion(), rear.getAvgCongestion()) ?
                Math.max(front.getMaxCongestion(), rear.getMaxCongestion()) : -1;

        return new ActualPath(totalPath, totalCompressedPath, totalMoveInfos, congestionScore, avgCongestion, maxCongestion);
    }

    /**
     * 실제 경로 목록을 우선순위에 맞게 정렬하여, 최적의 실제 경로를 생성
     *
     * @param actualPaths         실제 경로 목록
     * @param dateType            요일 종류
     * @param congestionThreshold 혼잡도 임계값
     * @param sortType            정렬 종류
     * @return 최적의 실제 경로
     */
    public ActualPath createOptimalPath(ActualPaths actualPaths, String dateType, int congestionThreshold, SortType sortType) {
        if (sortType == SortType.CONGESTION) {
            stationCongestionMap.setCongestionScoreOfPaths(actualPaths, dateType, congestionThreshold);
        }
        actualPaths.sort(sortType);
        return actualPaths.getOptimalPath();
    }

    private boolean isValidCongestion(int frontCongestion, int rearCongestion) {
        return frontCongestion != -1 && rearCongestion != -1;
    }
}

package kuraeyong.backend.service;

import kuraeyong.backend.common.exception.ErrorMessage;
import kuraeyong.backend.domain.constant.SortType;
import kuraeyong.backend.domain.path.MetroNodeWithEdge;
import kuraeyong.backend.domain.path.MetroPath;
import kuraeyong.backend.domain.path.MoveInfos;
import kuraeyong.backend.domain.path.PathResult;
import kuraeyong.backend.domain.path.PathResults;
import kuraeyong.backend.domain.path.UserMoveInfos;
import kuraeyong.backend.domain.station.congestion.StationCongestionMap;
import kuraeyong.backend.domain.station.info.MinimumStationInfo;
import kuraeyong.backend.manager.TemporaryPathsTopManager;
import kuraeyong.backend.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PathService {

    private final TemporaryPathsTopManager temporaryPathsTopManager;
    private final StationService stationService;
    private final MoveService moveService;
    private final StationCongestionMap stationCongestionMap;

    public PathResult searchPath(String orgStinNm, String destStinNm, String dateType, int hour, int min, int congestionThreshold, String convenience, PathResult front, int stopoverTime, String sortType) {
        validateExistStinNm(orgStinNm);
        validateExistStinNm(destStinNm);

        MinimumStationInfo org = stationService.getStationByName(orgStinNm);
        MinimumStationInfo dest = stationService.getStationByName(destStinNm);
        List<MetroPath> temporaryPaths = temporaryPathsTopManager.create(org, dest, dateType);
        PathResults pathResults = createPathResults(temporaryPaths, dateType, hour, min, front, stopoverTime);
        stationCongestionMap.setCongestionScoreOfPaths(pathResults, dateType, congestionThreshold);
        pathResults.sort(SortType.parse(sortType));

        return pathResults.getOptimalPath();
    }

    /**
     * 간이 경로 목록과 시간 정보를 이용해 실제 경로 목록을 생성
     *
     * @param temporaryPaths 간이 경로 목록
     * @param dateType       요일 정보
     * @param hour           시간
     * @param min            분
     * @return 실제 경로 목록
     */
    private PathResults createPathResults(List<MetroPath> temporaryPaths, String dateType, int hour, int min, PathResult front, int stopoverTime) {
        PathResults pathResults = new PathResults();
        for (MetroPath path : temporaryPaths) {
            path.setDirection();
            MetroPath compressedPath = path.getCompressPath();
            MoveInfos moveInfos = moveService.createMoveInfos(compressedPath, dateType, hour, min, front, stopoverTime);
            if (moveInfos == null) {
                continue;
            }
            setPassingTimeOfPath(path, compressedPath);
            pathResults.add(new PathResult(path, moveInfos));
        }
        return pathResults;
    }

    public PathResult join(PathResult front, PathResult rear, String dateType) {
        // 일반 경로 합치기
        MetroPath totalPath = new MetroPath(front.getPath());
        totalPath.concat(rear.getPath(), false);

        // 압축 경로 합치기
        MetroPath totalCompressedPath = new MetroPath(front.getCompressedPath());
        totalCompressedPath.concat(rear.getCompressedPath(), false);

        // 이동 정보 합치기
        MoveInfos totalMoveInfos = moveService.join(front.getMoveInfos(), rear.getMoveInfos(), dateType);

        // 혼잡도 점수 계산
        int congestionScore = (front.getCongestionScore() + rear.getCongestionScore()) / 2;

        return new PathResult(totalPath, totalCompressedPath, totalMoveInfos, congestionScore);
    }

    public UserMoveInfos createUserMoveInfos(PathResult pathResult, String stopoverStinNm, int stopoverTime) {
        return moveService.createUserMoveInfos(pathResult, stopoverStinNm, stopoverTime);
    }

    /**
     * 압축 경로 경유역의 passingTime을 기반으로, 일반 경로 모든 역의 passingTime을 설정
     *
     * @param path           일반 경로
     * @param compressedPath 압축 경로
     */
    private void setPassingTimeOfPath(MetroPath path, MetroPath compressedPath) {
        // 압축 경로 경유역의 passingTime을 일반 경로의 해당역에도 반영
        for (MetroNodeWithEdge compressedPathNode : compressedPath.getPath()) {
            for (MetroNodeWithEdge node : path.getPath()) {
                if (!MinimumStationInfo.get(node).equals(MinimumStationInfo.get(compressedPathNode))) {
                    continue;
                }
                node.setPassingTime(compressedPathNode.getPassingTime());
            }
        }

        // 일반 경로 모든역의 passingTime 설정
        int size = path.size();
        int lastPassingTimeIdx = 0;
        for (int i = 1; i < size; i++) {
            MetroNodeWithEdge curr = path.get(i);
            if (curr.getPassingTime() == null) {
                continue;
            }
            // i가 압축 경로 경유역인 경우
            int increment = DateUtil.getMinDiff(path.get(lastPassingTimeIdx).getPassingTime(), curr.getPassingTime()) / (i - lastPassingTimeIdx);
            for (int j = lastPassingTimeIdx + 1, incrementCnt = 1; j < i; j++) {
                path.get(j).setPassingTime(DateUtil.plusMinutes(path.get(lastPassingTimeIdx).getPassingTime(), increment * incrementCnt++));
            }
            lastPassingTimeIdx = i;
        }

        // 압축 경로의 마지막 경유역 처리
        MetroNodeWithEdge lastPassingTimeNode = path.get(lastPassingTimeIdx);
        MetroNodeWithEdge lastNode = path.get(size - 1);
        int increment = DateUtil.getMinDiff(lastPassingTimeNode.getPassingTime(), lastNode.getPassingTime()) / (size - lastPassingTimeIdx);
        for (int j = lastPassingTimeIdx + 1, incrementCnt = 1; j < size; j++) {
            path.get(j).setPassingTime(DateUtil.plusMinutes(lastPassingTimeNode.getPassingTime(), increment * incrementCnt++));
        }
    }

    private void validateExistStinNm(String stinNm) {
        if (stationService.getStationByName(stinNm) == null) {
            throw new IllegalArgumentException(ErrorMessage.STATION_NOT_FOUND.get());
        }
    }
}

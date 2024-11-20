package kuraeyong.backend.service;

import kuraeyong.backend.common.exception.ErrorMessage;
import kuraeyong.backend.domain.constant.SortType;
import kuraeyong.backend.domain.path.ActualPath;
import kuraeyong.backend.domain.path.ActualPaths;
import kuraeyong.backend.domain.path.MetroPath;
import kuraeyong.backend.domain.path.UserMoveInfos;
import kuraeyong.backend.domain.station.congestion.StationCongestionMap;
import kuraeyong.backend.domain.station.info.MinimumStationInfo;
import kuraeyong.backend.manager.ActualPathsManager;
import kuraeyong.backend.manager.TemporaryPathsTopManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PathService {

    private final TemporaryPathsTopManager temporaryPathsTopManager;
    private final ActualPathsManager actualPathsManager;
    private final StationService stationService;
    private final MoveService moveService;
    private final StationCongestionMap stationCongestionMap;

    public ActualPath searchPath(String orgStinNm, String destStinNm, String dateType, int hour, int min, int congestionThreshold, String convenience, ActualPath front, int stopoverTime, String sortType) {
        validateExistStinNm(orgStinNm);
        validateExistStinNm(destStinNm);

        MinimumStationInfo org = stationService.getStationByName(orgStinNm);
        MinimumStationInfo dest = stationService.getStationByName(destStinNm);
        List<MetroPath> temporaryPaths = temporaryPathsTopManager.create(org, dest, dateType);
        ActualPaths actualPaths = actualPathsManager.create(temporaryPaths, dateType, hour, min, front, stopoverTime);
        stationCongestionMap.setCongestionScoreOfPaths(actualPaths, dateType, congestionThreshold);
        actualPaths.sort(SortType.parse(sortType));

        return actualPaths.getOptimalPath();
    }

    public ActualPath join(ActualPath front, ActualPath rear, String dateType) {
        return actualPathsManager.join(front, rear, dateType);
    }

    public UserMoveInfos createUserMoveInfos(ActualPath actualPath, String stopoverStinNm, int stopoverTime) {
        return moveService.createUserMoveInfos(actualPath, stopoverStinNm, stopoverTime);
    }

    private void validateExistStinNm(String stinNm) {
        if (stationService.getStationByName(stinNm) == null) {
            throw new IllegalArgumentException(ErrorMessage.STATION_NOT_FOUND.get());
        }
    }
}

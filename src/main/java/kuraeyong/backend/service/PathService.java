package kuraeyong.backend.service;

import kuraeyong.backend.common.exception.ErrorMessage;
import kuraeyong.backend.common.exception.PathSearchResultException;
import kuraeyong.backend.common.response.BaseResponse;
import kuraeyong.backend.common.response.ResponseStatus;
import kuraeyong.backend.domain.constant.SortType;
import kuraeyong.backend.domain.path.ActualPath;
import kuraeyong.backend.domain.path.ActualPaths;
import kuraeyong.backend.domain.path.MetroPath;
import kuraeyong.backend.domain.path.PathSearchResult;
import kuraeyong.backend.domain.station.info.MinimumStationInfo;
import kuraeyong.backend.dto.PathSearchResultDto;
import kuraeyong.backend.manager.path.ActualPathsManager;
import kuraeyong.backend.manager.path.PathSearchResultManager;
import kuraeyong.backend.manager.path.TemporaryPathsTopManager;
import kuraeyong.backend.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PathService {

    private final TemporaryPathsTopManager temporaryPathsTopManager;
    private final ActualPathsManager actualPathsManager;
    private final PathSearchResultManager pathSearchResultManager;
    private final StationService stationService;

    /**
     * 경유역이 지정되지 않은 경로 탐색 요청을 수행하여, 경로 탐색 결과를 반환
     *
     * @param pathSearchRequest 경로 탐색 요청
     * @return 경로 탐색 결과
     */
    public ResponseStatus searchDirectPath(PathSearchResultDto.Request pathSearchRequest) {
        ActualPath optimalPath = createOptimalPath(pathSearchRequest.getOrgStinNm(),
                pathSearchRequest.getDestStinNm(),
                pathSearchRequest.getDateType(),
                pathSearchRequest.getHour(),
                pathSearchRequest.getMin(),
                pathSearchRequest.getCongestionThreshold(),
                null,
                -1,
                pathSearchRequest.getSortType());
        PathSearchResult pathSearchResult = pathSearchResultManager.create(optimalPath, null, -1);
        System.out.println(pathSearchResult);
        return new BaseResponse<>(new PathSearchResultDto.Response(pathSearchResult));
    }

    /**
     * 경유역이 지정된 경로 탐색 요청을 수행하여, 경로 탐색 결과를 반환
     *
     * @param pathSearchRequest 경로 탐색 요청
     * @return 경로 탐색 결과
     */
    public ResponseStatus searchIndirectPath(PathSearchResultDto.Request pathSearchRequest) {
        ActualPath front = createOptimalPath(pathSearchRequest.getOrgStinNm(),
                pathSearchRequest.getStopoverStinNm(),
                pathSearchRequest.getDateType(),
                pathSearchRequest.getHour(),
                pathSearchRequest.getMin(),
                pathSearchRequest.getCongestionThreshold(),
                null,
                -1,
                pathSearchRequest.getSortType());
        String stopoverDptTm = front.getFinalArvTm();
        ActualPath rear = createOptimalPath(pathSearchRequest.getStopoverStinNm(),
                pathSearchRequest.getDestStinNm(),
                pathSearchRequest.getDateType(),
                DateUtil.getHour(stopoverDptTm),
                DateUtil.getMinute(stopoverDptTm),
                pathSearchRequest.getCongestionThreshold(),
                front,
                pathSearchRequest.getStopoverTime(),
                pathSearchRequest.getSortType());
        ActualPath optimalPath = actualPathsManager.join(front, rear, pathSearchRequest.getDateType());
        PathSearchResult pathSearchResult = pathSearchResultManager.create(optimalPath, pathSearchRequest.getStopoverStinNm(), pathSearchRequest.getStopoverTime());
        System.out.println(pathSearchResult);
        return new BaseResponse<>(new PathSearchResultDto.Response(pathSearchResult));
    }

    /**
     * 출발역부터 도착역까지의 최적의 실제 경로를 생성
     *
     * @param orgStinNm           출발역 이름
     * @param destStinNm          도착역 이름
     * @param dateType            요일 종류
     * @param hour                시간
     * @param min                 분
     * @param congestionThreshold 혼잡도 임계값
     * @param front               특정 역을 경유하는 경로 탐색인 경우, 출발역에서 경유역까지의 실제 경로
     * @param stopoverTime        경유역에서 경유하는 시간
     * @param sortType            정렬 종류 (최적의 경로를 찾기 위해 필요)
     * @return 최적의 실제 경로
     */
    private ActualPath createOptimalPath(String orgStinNm, String destStinNm, String dateType, int hour, int min, int congestionThreshold, ActualPath front, int stopoverTime, String sortType) {
        MinimumStationInfo org = stationService.getStationByName(orgStinNm);
        MinimumStationInfo dest = stationService.getStationByName(destStinNm);
        List<MetroPath> temporaryPaths = temporaryPathsTopManager.create(org, dest, dateType);
        ActualPaths actualPaths = actualPathsManager.create(temporaryPaths, dateType, hour, min, front, stopoverTime);
        ActualPath optimalPath = actualPathsManager.createOptimalPath(actualPaths, dateType, congestionThreshold, SortType.parse(sortType));
        if (optimalPath == null) {
            throw new PathSearchResultException(ErrorMessage.PATH_SEARCH_RESULT_NOT_FOUND);
        }
        return optimalPath;
    }
}

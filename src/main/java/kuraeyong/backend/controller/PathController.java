package kuraeyong.backend.controller;

import kuraeyong.backend.common.response.BaseResponse;
import kuraeyong.backend.common.response.status.ResponseStatus;
import kuraeyong.backend.common.response.status.ResponseStatusType;
import kuraeyong.backend.domain.path.PathResult;
import kuraeyong.backend.domain.path.UserMoveInfos;
import kuraeyong.backend.dto.request.PathSearchRequest;
import kuraeyong.backend.dto.response.UserMoveInfosDto;
import kuraeyong.backend.service.PathService;
import kuraeyong.backend.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/paths")
public class PathController {

    private final PathService pathService;

    /**
     * 환승역 번호   0~268 (총 환승역 269개)
     */
    @PostMapping("")
    public ResponseStatus searchPath(@RequestBody PathSearchRequest pathSearchRequest) {
        validatePathSearchRequest(pathSearchRequest);

        if (isDirectSearchPath(pathSearchRequest.getStopoverStinNm())) {
            PathResult pathResult = pathService.searchPath(pathSearchRequest.getOrgStinNm(),
                    pathSearchRequest.getDestStinNm(),
                    pathSearchRequest.getDateType(),
                    pathSearchRequest.getHour(),
                    pathSearchRequest.getMin(),
                    pathSearchRequest.getCongestionThreshold(),
                    pathSearchRequest.getConvenience(),
                    null,
                    -1,
                    pathSearchRequest.getSortType());
            if (isEmpty(pathResult)) {
                return new BaseResponse<>(ResponseStatusType.TRAIN_NOT_EXISTED);
            }
            UserMoveInfos userMoveInfos = pathService.createUserMoveInfos(pathResult, null, -1);
            System.out.println(userMoveInfos);
            return new BaseResponse<>(new UserMoveInfosDto(userMoveInfos));
        }
        PathResult pathResultBeforeStopoverStin = pathService.searchPath(pathSearchRequest.getOrgStinNm(),
                pathSearchRequest.getStopoverStinNm(),
                pathSearchRequest.getDateType(),
                pathSearchRequest.getHour(),
                pathSearchRequest.getMin(),
                pathSearchRequest.getCongestionThreshold(),
                pathSearchRequest.getConvenience(),
                null,
                -1,
                pathSearchRequest.getSortType());
        if (isEmpty(pathResultBeforeStopoverStin)) {
            return new BaseResponse<>(ResponseStatusType.TRAIN_NOT_EXISTED);
        }
        int stopoverTime = pathSearchRequest.getStopoverTime();
        String stopoverDptTm = pathResultBeforeStopoverStin.getFinalArvTm();
        PathResult pathResultAfterStopoverStin = pathService.searchPath(pathSearchRequest.getStopoverStinNm(),
                pathSearchRequest.getDestStinNm(),
                pathSearchRequest.getDateType(),
                DateUtil.getHour(stopoverDptTm),
                DateUtil.getMinute(stopoverDptTm),
                pathSearchRequest.getCongestionThreshold(),
                pathSearchRequest.getConvenience(),
                pathResultBeforeStopoverStin,
                stopoverTime,
                pathSearchRequest.getSortType());
        if (isEmpty(pathResultAfterStopoverStin)) {
            return new BaseResponse<>(ResponseStatusType.TRAIN_NOT_EXISTED);
        }
        PathResult totalPathResult = pathService.join(pathResultBeforeStopoverStin, pathResultAfterStopoverStin, pathSearchRequest.getDateType());
        UserMoveInfos userMoveInfos = pathService.createUserMoveInfos(totalPathResult, pathSearchRequest.getStopoverStinNm(), stopoverTime);
        System.out.println(userMoveInfos);
        return new BaseResponse<>(new UserMoveInfosDto(userMoveInfos));
    }

    private boolean isEmpty(PathResult pathResult) {
        return pathResult == null;
    }

    private boolean isDirectSearchPath(String stopoverStinNm) {
        return stopoverStinNm == null;
    }

    private void validatePathSearchRequest(PathSearchRequest pathSearchRequest) {
        validateUniqueStinNm(pathSearchRequest.getOrgStinNm(), pathSearchRequest.getStopoverStinNm(), pathSearchRequest.getDestStinNm());
        validateDateType(pathSearchRequest.getDateType());
        validateTimeInfo(pathSearchRequest.getHour(), pathSearchRequest.getMin());
    }

    private void validateTimeInfo(int hour, int min) {
        boolean isValidHour = (hour >= 0 && hour < 24);
        boolean isValidMin = (min >= 0 && min < 60);
        if (!isValidHour || !isValidMin) {
            throw new IllegalArgumentException("시간 정보가 유효하지 않습니다.");
        }
    }

    private void validateDateType(String dateType) {
        if (!dateType.equals("평일") && !dateType.equals("토") && !dateType.equals("휴일")) {
            throw new IllegalArgumentException("요일 종류가 유효하지 않습니다.");
        }
    }

    private void validateUniqueStinNm(String orgStinNm, String stopoverStinNm, String destStinNm) {
        if (isDirectSearchPath(stopoverStinNm)) {
            return;
        }
        if (orgStinNm.equals(stopoverStinNm)) {
            throw new IllegalArgumentException("출발역과 경유역의 이름이 동일합니다.");
        }
        if (orgStinNm.equals(destStinNm)) {
            throw new IllegalArgumentException("출발역과 도착역의 이름이 동일합니다.");
        }
        if (stopoverStinNm.equals(destStinNm)) {
            throw new IllegalArgumentException("경유역과 도착역의 이름이 동일합니다.");
        }
    }
}

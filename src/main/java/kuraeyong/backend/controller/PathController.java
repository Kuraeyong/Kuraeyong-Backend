package kuraeyong.backend.controller;

import kuraeyong.backend.common.exception.ErrorMessage;
import kuraeyong.backend.common.response.ResponseStatus;
import kuraeyong.backend.dto.UserMoveInfosDto;
import kuraeyong.backend.service.PathService;
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

    @PostMapping("")
    public ResponseStatus searchPath(@RequestBody UserMoveInfosDto.Request pathSearchRequest) {
        validatePathSearchRequest(pathSearchRequest);
        if (isDirectSearchPath(pathSearchRequest.getStopoverStinNm())) {
            return pathService.searchDirectPath(pathSearchRequest);
        }
        return pathService.searchIndirectPath(pathSearchRequest);
    }

    private boolean isDirectSearchPath(String stopoverStinNm) {
        return stopoverStinNm == null;
    }

    private void validatePathSearchRequest(UserMoveInfosDto.Request pathSearchRequest) {
        validateUniqueStinNm(pathSearchRequest.getOrgStinNm(), pathSearchRequest.getStopoverStinNm(), pathSearchRequest.getDestStinNm());
        validateDateType(pathSearchRequest.getDateType());
        validateTimeInfo(pathSearchRequest.getHour(), pathSearchRequest.getMin());
    }

    private void validateTimeInfo(int hour, int min) {
        boolean isValidHour = (hour >= 0 && hour < 24);
        boolean isValidMin = (min >= 0 && min < 60);
        if (!isValidHour || !isValidMin) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_TIME_INFO.get());
        }
    }

    private void validateDateType(String dateType) {
        if (!dateType.equals("평일") && !dateType.equals("토") && !dateType.equals("휴일")) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_DATE_TYPE.get());
        }
    }

    private void validateUniqueStinNm(String orgStinNm, String stopoverStinNm, String destStinNm) {
        if (isDirectSearchPath(stopoverStinNm)) {
            return;
        }
        if (orgStinNm.equals(stopoverStinNm)) {
            throw new IllegalArgumentException(ErrorMessage.DUPLICATE_ORG_AND_STOPOVER.get());
        }
        if (orgStinNm.equals(destStinNm)) {
            throw new IllegalArgumentException(ErrorMessage.DUPLICATE_ORG_AND_DEST.get());
        }
        if (stopoverStinNm.equals(destStinNm)) {
            throw new IllegalArgumentException(ErrorMessage.DUPLICATE_STOPOVER_AND_DEST.get());
        }
    }
}

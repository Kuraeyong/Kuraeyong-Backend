package kuraeyong.backend.common.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ErrorMessage {

    /**
     * ILLEGAL_ARGUMENT_EXCEPTION
     */
    DUPLICATE_ORG_AND_STOPOVER("출발역과 경유역의 이름이 동일합니다."),
    DUPLICATE_ORG_AND_DEST("출발역과 도착역의 이름이 동일합니다."),
    DUPLICATE_STOPOVER_AND_DEST("경유역과 도착역의 이름이 동일합니다."),
    INVALID_TIME_INFO("시간 정보가 유효하지 않습니다."),
    INVALID_DATE_TYPE("요일 종류가 유효하지 않습니다."),
    STATION_NOT_FOUND("존재하지 않는 역입니다."),
    CONVENIENCE_NOT_FOUND("존재하지 않는 편의시설입니다."),

    /**
     * PATH_RESULT_EXCEPTION
     */
    TEMPORARY_PATH_NOT_FOUND("임시 경로가 존재하지 않습니다."),
    PATH_SEARCH_RESULT_NOT_FOUND("경로 탐색 결과가 존재하지 않습니다.");

    private final String errorMessage;

    public String get() {
        return errorMessage;
    }
}

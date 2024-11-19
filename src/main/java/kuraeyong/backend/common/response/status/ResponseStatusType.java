package kuraeyong.backend.common.response.status;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ResponseStatusType implements ResponseStatus {
    SUCCESS(1000, HttpStatus.OK.value(), "요청에 성공하였습니다."),

    TRAIN_NOT_EXISTED(2000, HttpStatus.BAD_REQUEST.value(), "현재 운행 중인 열차가 없습니다.");

    private final int code;
    private final int status;
    private final String message;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

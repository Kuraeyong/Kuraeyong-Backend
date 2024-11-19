package kuraeyong.backend.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResponseStatusType implements ResponseStatus {
    SUCCESS(1000, HttpStatus.OK.value(), "요청에 성공하였습니다."),
    BAD_REQUEST(2000, HttpStatus.BAD_REQUEST.value(), "유효하지 않은 요청입니다."),
    NOT_FOUND(3000, HttpStatus.NOT_FOUND.value(), "결과가 존재하지 않습니다.");

    private final int code;
    private final int status;
    private final String message;
}

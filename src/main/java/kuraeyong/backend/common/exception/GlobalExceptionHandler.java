package kuraeyong.backend.common.exception;

import kuraeyong.backend.common.response.BaseErrorResponse;
import kuraeyong.backend.common.response.ResponseStatus;
import kuraeyong.backend.common.response.ResponseStatusType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    private ResponseStatus handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage());
        return new BaseErrorResponse(ResponseStatusType.BAD_REQUEST, e);
    }

    @ExceptionHandler(PathSearchResultException.class)
    private ResponseStatus handlePathSearchResultException(PathSearchResultException e) {
        log.error(e.getMessage());
        return new BaseErrorResponse(ResponseStatusType.NOT_FOUND, e);
    }

    @ExceptionHandler(DomainInitializationException.class)
    private ResponseStatus handleDomainInitializationException(DomainInitializationException e) {
        log.error(e.getMessage());
        return new BaseErrorResponse(ResponseStatusType.INTERNAL_SERVER_ERROR, e);
    }
}

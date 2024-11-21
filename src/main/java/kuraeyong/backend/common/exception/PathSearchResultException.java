package kuraeyong.backend.common.exception;

public class PathSearchResultException extends RuntimeException {

    public PathSearchResultException(ErrorMessage errorMessage) {
        super(errorMessage.get());
    }
}

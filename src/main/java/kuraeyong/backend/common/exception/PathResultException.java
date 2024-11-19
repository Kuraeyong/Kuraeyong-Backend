package kuraeyong.backend.common.exception;

public class PathResultException extends RuntimeException {

    public PathResultException(ErrorMessage errorMessage) {
        super(errorMessage.get());
    }
}

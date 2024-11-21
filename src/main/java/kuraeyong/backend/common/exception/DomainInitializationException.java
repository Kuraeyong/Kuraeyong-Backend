package kuraeyong.backend.common.exception;

public class DomainInitializationException extends RuntimeException {

    public DomainInitializationException(ErrorMessage errorMessage) {
        super(errorMessage.get());
    }
}

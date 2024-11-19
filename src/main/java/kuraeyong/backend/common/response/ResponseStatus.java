package kuraeyong.backend.common.response;

public interface ResponseStatus {
    int getCode();

    int getStatus();

    String getMessage();
}

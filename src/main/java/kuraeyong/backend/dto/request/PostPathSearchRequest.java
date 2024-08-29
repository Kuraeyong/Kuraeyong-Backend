package kuraeyong.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostPathSearchRequest {
    private String orgStinNm;
    private String destStinNm;
    private String dateType;
    private int hour;
    private int min;

    @Override
    public String toString() {
        return "PostPathSearchRequest{" +
                "orgStinNm='" + orgStinNm + '\'' +
                ", destStinNm='" + destStinNm + '\'' +
                ", dateType='" + dateType + '\'' +
                ", hour=" + hour +
                ", min=" + min +
                '}';
    }
}

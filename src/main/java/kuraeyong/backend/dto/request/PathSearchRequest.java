package kuraeyong.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PathSearchRequest {
    private String orgStinNm;
    private String stopoverStinNm;
    private String destStinNm;
    private String dateType;
    private int hour;
    private int min;
    private int congestionThreshold;
    private String convenience;
    private int stopoverTime;

    @Override
    public String toString() {
        return "PathSearchRequest{" +
                "orgStinNm='" + orgStinNm + '\'' +
                ", stopoverStinNm='" + stopoverStinNm + '\'' +
                ", destStinNm='" + destStinNm + '\'' +
                ", dateType='" + dateType + '\'' +
                ", hour=" + hour +
                ", min=" + min +
                ", congestionThreshold=" + congestionThreshold +
                ", convenience='" + convenience + '\'' +
                ", stopoverTime=" + stopoverTime +
                '}';
    }
}

package kuraeyong.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MoveInfo {
    private String lnCd;
    private String trnNo;
    private String dptTm;   // 현재역 출발 시간
    private String arvTm;   // 다음역 도착 시간

    @Override
    public String toString() {
        return "MoveInfo{" +
                "lnCd='" + lnCd + '\'' +
                ", trnNo='" + trnNo + '\'' +
                ", dptTm='" + dptTm + '\'' +
                ", arvTm='" + arvTm + '\'' +
                '}';
    }
}

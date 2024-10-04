package kuraeyong.backend.domain.path;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MoveInfo {
    private String lnCd;
    @Setter
    private String trnNo;
    @Setter
    private String dptTm;   // 현재역 출발 시간
    @Setter
    private String arvTm;   // 다음역 도착 시간
    @Setter
    private int trnGroupNo;

    @Override
    public String toString() {
        return "MoveInfo{" +
                "lnCd='" + lnCd + '\'' +
                ", trnNo='" + trnNo + '\'' +
                ", dptTm='" + dptTm + '\'' +
                ", arvTm='" + arvTm + '\'' +
                ", trnGroupNo=" + trnGroupNo +
                '}';
    }
}

package kuraeyong.backend.domain.path;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoveInfo {
    @Setter(AccessLevel.NONE)
    private String lnCd;
    private String tmnStinNm;
    private String trnNo;
    private String dptTm;   // 현재역 출발 시간
    private String arvTm;   // 다음역 도착 시간
    private int trnGroupNo;

    public MoveInfo(MoveInfo moveInfo) {
        this.lnCd = moveInfo.lnCd;
        this.tmnStinNm = moveInfo.tmnStinNm;
        this.trnNo = moveInfo.trnNo;
        this.dptTm = moveInfo.dptTm;
        this.arvTm = moveInfo.arvTm;
        this.trnGroupNo = moveInfo.trnGroupNo;
        ;
    }

    @Override
    public String toString() {
        return "MoveInfo{" +
                "lnCd='" + lnCd + '\'' +
                ", tmnStinNm='" + tmnStinNm + '\'' +
                ", trnNo='" + trnNo + '\'' +
                ", dptTm='" + dptTm + '\'' +
                ", arvTm='" + arvTm + '\'' +
                ", trnGroupNo=" + trnGroupNo +
                '}';
    }
}

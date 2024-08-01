package kuraeyong.backend.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationTimeTableDto {
    private String lnCd;
    private String orgStinCd;
    private String dayCd;
    private String arvTm;
    private String dayNm;
    private String dptTm;
    private String stinCd;
    private String trnNo;
    private String tmnStinCd;
    private String railOprIsttCd;

    @Override
    public String toString() {
        return "StationTimeTableDto{" +
                "lnCd='" + lnCd + '\'' +
                ", orgStinCd='" + orgStinCd + '\'' +
                ", dayCd='" + dayCd + '\'' +
                ", arvTm='" + arvTm + '\'' +
                ", dayNm='" + dayNm + '\'' +
                ", dptTm='" + dptTm + '\'' +
                ", stinCd='" + stinCd + '\'' +
                ", trnNo='" + trnNo + '\'' +
                ", tmnStinCd='" + tmnStinCd + '\'' +
                ", railOprIsttCd='" + railOprIsttCd + '\'' +
                '}';
    }
}

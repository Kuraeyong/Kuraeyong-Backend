package kuraeyong.backend.dto;

import kuraeyong.backend.domain.StationTimeTableElement;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationTimeTableElementDto {
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
        return "StationTimeTableElementDto{" +
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

    public StationTimeTableElement toEntity() {
        return StationTimeTableElement.builder()
                .lnCd(lnCd)
                .orgStinCd(orgStinCd)
                .dayCd(dayCd)
                .arvTm(arvTm)
                .dayNm(dayNm)
                .dptTm(dptTm)
                .stinCd(stinCd)
                .trnNo(trnNo)
                .tmnStinCd(tmnStinCd)
                .railOprIsttCd(railOprIsttCd)
                .build();
    }
}

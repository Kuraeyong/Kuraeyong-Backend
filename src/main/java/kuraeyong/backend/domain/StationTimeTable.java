package kuraeyong.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StationTimeTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String lnCd;

    @Column
    private String orgStinCd;

    @Column
    private String dayCd;

    @Column
    private String arvTm;

    @Column
    private String dayNm;

    @Column
    private String dptTm;

    @Column
    private String stinCd;

    @Column
    private String trnNo;

    @Column
    private String tmnStinCd;

    @Column
    private String railOprIsttCd;

    @Override
    public String toString() {
        return "StationTimeTable{" +
                "id=" + id +
                ", lnCd='" + lnCd + '\'' +
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

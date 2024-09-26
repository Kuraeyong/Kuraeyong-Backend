package kuraeyong.backend.domain;

import jakarta.persistence.*;
import kuraeyong.backend.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StationTimeTableElement implements Comparable<StationTimeTableElement> {

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
        return "StationTimeTableElement{" +
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

    /**
     * a.compareTo(b)
     * a > b    return pos
     * a == b   return 0
     * a < b    return neg
     */
    @Override
    public int compareTo(StationTimeTableElement o) {
        int time1 = DateUtil.getTimeForCompare(this.arvTm, this.dptTm);
        int time2 = DateUtil.getTimeForCompare(o.arvTm, o.dptTm);

        return Integer.compare(time1, time2);
    }

    // 기점이면 (엄밀하게는, 도착시간이 유효한 시간 정보가 아니라면)
    public boolean isOrgStin() {
        return DateUtil.isValidTime(arvTm);
    }

    // 종점이면 (엄밀하게는, 출발시간이 유효한 시간 정보가 아니라면)
    public boolean isTmnStin() {
        return DateUtil.isValidTime(dptTm);
    }
}

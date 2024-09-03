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
        int time1 = getTimeForCompare(this.arvTm, this.dptTm);
        int time2 = getTimeForCompare(o.arvTm, o.dptTm);

        return Integer.compare(time1, time2);
    }

    private boolean isNull(String str) {
        return !str.matches("[0-9]{6}");
    }

    private int getTimeForCompare(String arvTm, String dptTm) {
        int time = isNull(dptTm) ? Integer.parseInt(arvTm) : Integer.parseInt(dptTm);

        return (time < 30000) ? time + 240000 : time;
    }
}

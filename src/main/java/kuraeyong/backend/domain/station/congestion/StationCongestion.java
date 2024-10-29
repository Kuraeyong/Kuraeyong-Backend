package kuraeyong.backend.domain.station.congestion;

import jakarta.persistence.*;
import kuraeyong.backend.domain.station.time_table.StationTimeTableElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StationCongestion implements Comparable<StationCongestion> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String railOprIsttCd;

    @Column
    private String lnCd;

    @Column
    private String stinCd;

    @Column
    private String stinNm;

    @Column
    private String dayNm;

    @Column
    private String upOrDown;

    @Column
    private int isExpTrn;

    @Column
    private double time_0530;

    @Column
    private double time_0600;

    @Column
    private double time_0630;

    @Column
    private double time_0700;

    @Column
    private double time_0730;

    @Column
    private double time_0800;

    @Column
    private double time_0830;

    @Column
    private double time_0900;

    @Column
    private double time_0930;

    @Column
    private double time_1000;

    @Column
    private double time_1030;

    @Column
    private double time_1100;

    @Column
    private double time_1130;

    @Column
    private double time_1200;

    @Column
    private double time_1230;

    @Column
    private double time_1300;

    @Column
    private double time_1330;

    @Column
    private double time_1400;

    @Column
    private double time_1430;

    @Column
    private double time_1500;

    @Column
    private double time_1530;

    @Column
    private double time_1600;

    @Column
    private double time_1630;

    @Column
    private double time_1700;

    @Column
    private double time_1730;

    @Column
    private double time_1800;

    @Column
    private double time_1830;

    @Column
    private double time_1900;

    @Column
    private double time_1930;

    @Column
    private double time_2000;

    @Column
    private double time_2030;

    @Column
    private double time_2100;

    @Column
    private double time_2130;

    @Column
    private double time_2200;

    @Column
    private double time_2230;

    @Column
    private double time_2300;

    @Column
    private double time_2330;

    @Column
    private double time_0000;

    @Column
    private double time_0030;

    @Override
    public int compareTo(StationCongestion o) {
        return dayNm.compareTo(o.dayNm);
    }

    public double getTime(String time) {
        return switch (time) {
            case "time_0530" -> time_0530;
            case "time_0600" -> time_0600;
            case "time_0630" -> time_0630;
            case "time_0700" -> time_0700;
            case "time_0730" -> time_0730;
            case "time_0800" -> time_0800;
            case "time_0830" -> time_0830;
            case "time_0900" -> time_0900;
            case "time_0930" -> time_0930;
            case "time_1000" -> time_1000;
            case "time_1030" -> time_1030;
            case "time_1100" -> time_1100;
            case "time_1130" -> time_1130;
            case "time_1200" -> time_1200;
            case "time_1230" -> time_1230;
            case "time_1300" -> time_1300;
            case "time_1330" -> time_1330;
            case "time_1400" -> time_1400;
            case "time_1430" -> time_1430;
            case "time_1500" -> time_1500;
            case "time_1530" -> time_1530;
            case "time_1600" -> time_1600;
            case "time_1630" -> time_1630;
            case "time_1700" -> time_1700;
            case "time_1730" -> time_1730;
            case "time_1800" -> time_1800;
            case "time_1830" -> time_1830;
            case "time_1900" -> time_1900;
            case "time_1930" -> time_1930;
            case "time_2000" -> time_2000;
            case "time_2030" -> time_2030;
            case "time_2100" -> time_2100;
            case "time_2130" -> time_2130;
            case "time_2200" -> time_2200;
            case "time_2230" -> time_2230;
            case "time_2300" -> time_2300;
            case "time_2330" -> time_2330;
            case "time_0000" -> time_0000;
            case "time_0030" -> time_0030;
            default -> -1;
        };
    }
}

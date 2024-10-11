package kuraeyong.backend.domain.station.congestion;

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
public class StationCongestion {
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
}

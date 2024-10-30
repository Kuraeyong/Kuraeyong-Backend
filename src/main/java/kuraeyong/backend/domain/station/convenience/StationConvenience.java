package kuraeyong.backend.domain.station.convenience;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StationConvenience {
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
    private int elevator;   // 엘리베이터

    @Column
    private int disabledToilet; // 장애인화장실

    @Column
    private int lactationRoom;  // 수유실

    @Column
    private int wheelchairCharger;  // 휠체어충전기

    @Column
    private int wheelchairLift; // 휠체어리프트

    @Column
    private int mobileSafetyBoard;  // 이동식안전발판

    @Column
    private int infoCenter;  // 고객안내센터

    @Column
    private int lostAndFoundCenter; // 유실물센터

    @Column
    private int autoDisp;   // 무인민원발급기
}

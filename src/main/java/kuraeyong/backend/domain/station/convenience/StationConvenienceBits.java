package kuraeyong.backend.domain.station.convenience;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StationConvenienceBits {
    private boolean elevator;   // 엘리베이터
    private boolean disabledToilet; // 장애인화장실
    private boolean lactationRoom;  // 수유실
    private boolean wheelchairCharger;  // 휠체어충전기
    private boolean wheelchairLift; // 휠체어리프트
    private boolean mobileSafetyBoard;  // 이동식안전발판
    private boolean infoCenter;  // 고객안내센터
    private boolean lostAndFoundCenter; // 유실물센터
    private boolean autoDisp;   // 무인민원발급기

    public void operateBits(StationConvenienceBits o) {
        elevator |= o.elevator;
        disabledToilet |= o.disabledToilet;
        lactationRoom |= o.lactationRoom;
        wheelchairCharger |= o.wheelchairCharger;
        wheelchairLift |= o.wheelchairLift;
        mobileSafetyBoard |= o.mobileSafetyBoard;
        infoCenter |= o.infoCenter;
        lostAndFoundCenter |= o.lostAndFoundCenter;
        autoDisp |= o.autoDisp;
    }
}
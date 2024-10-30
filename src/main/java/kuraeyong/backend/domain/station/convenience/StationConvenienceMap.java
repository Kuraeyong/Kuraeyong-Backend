package kuraeyong.backend.domain.station.convenience;

import kuraeyong.backend.repository.StationConvenienceRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class StationConvenienceMap {
    private final HashMap<String, StationConvenienceBits> map;

    public StationConvenienceMap(StationConvenienceRepository stationConvenienceRepository) {
        map = new HashMap<>();

        for (StationConvenience row : stationConvenienceRepository.findAll()) {
            StationConvenienceBits stationConvenienceBits = StationConvenienceBits.builder()
                    .elevator(row.getElevator() == 1)
                    .disabledToilet(row.getDisabledToilet() == 1)
                    .lactationRoom(row.getLactationRoom() == 1)
                    .wheelchairCharger(row.getWheelchairCharger() == 1)
                    .wheelchairLift(row.getWheelchairLift() == 1)
                    .mobileSafetyBoard(row.getMobileSafetyBoard() == 1)
                    .infoCenter(row.getInfoCenter() == 1)
                    .lostAndFoundCenter(row.getLostAndFoundCenter() == 1)
                    .autoDisp(row.getAutoDisp() == 1)
                    .build();
            if (!map.containsKey(row.getStinNm())) {
                map.put(row.getStinNm(), stationConvenienceBits);
                return;
            }
            map.get(row.getStinNm()).operateBits(stationConvenienceBits);
        }
    }
}

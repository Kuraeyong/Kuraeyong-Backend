package kuraeyong.backend.domain.station.convenience;

import kuraeyong.backend.domain.constant.ConvenienceType;
import kuraeyong.backend.repository.StationConvenienceRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
                    .autoDispenser(row.getAutoDispenser() == 1)
                    .build();
            if (!map.containsKey(row.getStinNm())) {
                map.put(row.getStinNm(), stationConvenienceBits);
                continue;
            }
            map.get(row.getStinNm()).operateBits(stationConvenienceBits);
        }
    }

    public List<String> getStationsContainingConvenience(ConvenienceType convenienceType) {
        List<String> list = new ArrayList<>();
        map.keySet().forEach(key -> {
            if (map.get(key).contains(convenienceType)) {
                list.add(key);
            }
        });
        return list;
    }
}

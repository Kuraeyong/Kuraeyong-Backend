package kuraeyong.backend.domain.station.congestion;

import kuraeyong.backend.domain.constant.DirectionType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class StationCongestionList {
    private final List<StationCongestion> list;

    public StationCongestionList() {
        list = new ArrayList<>();
    }

    public void add(StationCongestion stationCongestion) {
        list.add(stationCongestion);
    }

    public int size() {
        return list.size();
    }

    public StationCongestion get(DirectionType directionType) {
        if (directionType == DirectionType.UP || directionType == DirectionType.UP_UP || directionType == DirectionType.DOWN_UP) {
            return list.get(0);
        }
        if (directionType == DirectionType.DOWN || directionType == DirectionType.UP_DOWN || directionType == DirectionType.DOWN_DOWN) {
            return list.get(1);
        }
        return null;
    }

    public void sort() {
        Collections.sort(list);
    }
}

package kuraeyong.backend.domain.station.congestion;

import java.util.ArrayList;
import java.util.List;

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

    public StationCongestion get(int idx) {
        return list.get(idx);
    }
}

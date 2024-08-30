package kuraeyong.backend.domain;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
public class StationTimeTable {
    private final List<StationTimeTableElement> list;

    public StationTimeTable() {
        list = new ArrayList<>();
    }

    public void add(StationTimeTableElement stationTimeTableElement) {
        list.add(stationTimeTableElement);
    }
}

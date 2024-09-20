package kuraeyong.backend.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class StationTrfWeightList {
    private final List<StationTrfWeight> list;

    public StationTrfWeightList() {
        list = new ArrayList<>();
    }

    public void add(StationTrfWeight stationTrfWeight) {
        list.add(stationTrfWeight);
    }

    public int size() {
        return list.size();
    }

    public StationTrfWeight get(int idx) {
        return list.get(idx);
    }
}
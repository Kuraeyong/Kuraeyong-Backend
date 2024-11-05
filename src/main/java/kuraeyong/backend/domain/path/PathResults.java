package kuraeyong.backend.domain.path;

import kuraeyong.backend.domain.constant.SortType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class PathResults {
    private final List<PathResult> list;

    public PathResults() {
        list = new ArrayList<>();
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public PathResult getOptimalPath() {
        if (isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public void sort(SortType sortType) {
        if (sortType == SortType.CONGESTION) {
            sortByCongestion();
        } else if (sortType == SortType.TRF_CNT) {
            sortByTrfCnt();
        } else {
            Collections.sort(list);
        }
    }

    private void sortByCongestion() {
        list.sort((o1, o2) -> {
            if (o1.getCongestionScore() == o2.getCongestionScore()) {
                return o1.compareTo(o2);
            }
            return o1.getCongestionScore() - o2.getCongestionScore();
        });
    }

    private void sortByTrfCnt() {
        list.sort((o1, o2) -> {
            if (o1.getTrfCnt() == o2.getTrfCnt()) {
                return o1.getTotalTrfTime() - o2.getTotalTrfTime();
            }
            return o1.getTrfCnt() - o2.getTrfCnt();
        });
    }

    public void add(PathResult pathResult) {
        list.add(pathResult);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (PathResult pathResult : list) {
            sb.append(pathResult).append('\n');
        }
        return sb.toString();
    }
}

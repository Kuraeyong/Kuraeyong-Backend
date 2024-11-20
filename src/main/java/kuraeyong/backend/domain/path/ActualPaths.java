package kuraeyong.backend.domain.path;

import kuraeyong.backend.domain.constant.SortType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class ActualPaths {
    private final List<ActualPath> list;

    public ActualPaths() {
        list = new ArrayList<>();
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public ActualPath getOptimalPath() {
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

    public void add(ActualPath actualPath) {
        list.add(actualPath);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ActualPath actualPath : list) {
            sb.append(actualPath).append('\n');
        }
        return sb.toString();
    }
}

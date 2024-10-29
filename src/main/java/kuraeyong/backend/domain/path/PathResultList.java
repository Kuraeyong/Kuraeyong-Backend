package kuraeyong.backend.domain.path;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Getter
public class PathResultList {
    private final List<PathResult> list;

    public PathResultList() {
        list = new ArrayList<>();
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public PathResult get(int idx) {
        return list.get(idx);
    }

    public void sort() {
        Collections.sort(list);
    }

    public void sortByCongestion() {
        list.sort((o1, o2) -> {
            if (o1.getAverageCongestion() == o2.getAverageCongestion()) {
                return o1.getMaxCongestion() - o2.getMaxCongestion();
            }
            return o1.getAverageCongestion() - o2.getAverageCongestion();
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

package kuraeyong.backend.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PathSearch {
    private final List<PathSearchElement> list;

    public PathSearch() {
        list = new ArrayList<>();
    }

    public int size() {
        return list.size();
    }

    public PathSearchElement get(int idx) {
        return list.get(idx);
    }

    public void sort() {
        Collections.sort(list);
    }

    public void add(PathSearchElement pathSearchElement) {
        list.add(pathSearchElement);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (PathSearchElement pathSearchElement : list) {
            sb.append(pathSearchElement).append('\n');
        }
        return sb.toString();
    }
}

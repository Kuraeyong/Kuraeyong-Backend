package kuraeyong.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class MetroPath implements Comparable<MetroPath> {
    private List<MetroNodeWithWeight> path;

    public MetroPath(MetroPath path) {
        this.path = new ArrayList<>();
        for (MetroNodeWithWeight node : path.getPath()) {
            addNode(new MetroNodeWithWeight(node));
        }
    }

    public void addNode(MetroNodeWithWeight node) {
        path.add(node);
    }

    public void concat(MetroPath path) {    // 깊은 복사
        for (MetroNodeWithWeight node : path.subPath(1, path.size()).getPath()) {
            addNode(new MetroNodeWithWeight(node));
        }
    }

    public double getPathWeight() {
        double sum = 0;
        for (MetroNodeWithWeight node : path) {
            sum += node.getWeight();
        }
        return sum;
    }

    public int size() {
        return path.size();
    }

    public MetroNodeWithWeight get(int idx) {
        return path.get(idx);
    }

    public MetroPath subPath(int start, int end) {
        return new MetroPath(path.subList(start, end));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (MetroNodeWithWeight node : path) {
            sb.append(node).append(' ');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetroPath metroPath = (MetroPath) o;
        return toString().equals(metroPath.toString());
    }

    /**
     * Set<MetroPath>에서 MetroPath 객체의 동등성을 판단
     */
    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    @Override
    public int compareTo(MetroPath o) {
        return Double.compare(this.getPathWeight(), o.getPathWeight());
    }
}

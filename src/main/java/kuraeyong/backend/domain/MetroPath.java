package kuraeyong.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class MetroPath {
    private List<MetroNodeWithWeight> path;

    public void addNode(MetroNodeWithWeight node) {
        path.add(node);
    }

    public double getPathWeight() {
        double sum = 0;
        for (MetroNodeWithWeight node : path) {
            sum += node.getWeight();
        }
        return sum;
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
}

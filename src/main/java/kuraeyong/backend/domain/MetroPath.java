package kuraeyong.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

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
}

package kuraeyong.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NodeForPathSearch implements Comparable<NodeForPathSearch> {
    private MetroNode node;
    private int weight;

    public int getNodeNo() {
        return node.getNodeNo();
    }

    public List<MetroEdge> getEdgeList() {
        return node.getEdgeList();
    }

    // 가중치를 기준으로 우선순위 결정
    @Override
    public int compareTo(NodeForPathSearch o) {
        return Integer.compare(this.weight, o.weight);
    }
}

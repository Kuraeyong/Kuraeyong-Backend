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

    public String getStinNm(int idx) {
        return path.get(idx).getStinNm();
    }

    public MetroPath subPath(int start, int end) {
        return new MetroPath(path.subList(start, end));
    }

    public void removeUnnecessaryPath() {
        // 출발역과 동일한 이름의 마지막 역의 인덱스 기록
        String orgStinNm = getStinNm(0);
        int lastIdxWithOrgNm = 0;
        for (int i = size() - 1; i > 0; i--) {
            if (getStinNm(i).equals(orgStinNm)) {   // 출발점 갱신
                get(i).setWeight(0);
                lastIdxWithOrgNm = i;
                break;
            }
        }

        // 도착역과 동일한 이름의 첫번째 역의 인덱스 기록
        String destStinNm = getStinNm(size() - 1);
        int firstIdxWithDestNm = size() - 1;
        for (int i = lastIdxWithOrgNm; i < size() - 1; i++) {
            if (getStinNm(i).equals(destStinNm)) {  // 도착점 갱신
                firstIdxWithDestNm = i;
                break;
            }
        }

        path = path.subList(lastIdxWithOrgNm, firstIdxWithDestNm + 1);

        // path를 한바퀴 돌면서 pprev prev curr
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

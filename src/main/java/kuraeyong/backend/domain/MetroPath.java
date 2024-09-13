package kuraeyong.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class MetroPath implements Comparable<MetroPath> {
    private List<MetroNodeWithEdge> path;

    public MetroPath(MetroPath path) {
        this.path = new ArrayList<>();
        for (MetroNodeWithEdge node : path.getPath()) {
            addNode(new MetroNodeWithEdge(node));
        }
    }

    public void addNode(MetroNodeWithEdge node) {
        path.add(node);
    }

    public void concat(MetroPath path) {    // 깊은 복사
        for (MetroNodeWithEdge node : path.subPath(1, path.size()).getPath()) {
            addNode(new MetroNodeWithEdge(node));
        }
    }

    public double getTotalWeight() {
        double sum = 0;
        for (MetroNodeWithEdge node : path) {
            sum += node.getWeight();
            sum += node.getWaitingTime();
        }
        return Math.round(sum * 10) / 10.0;
    }

    public int getTrfCnt() {
        int cnt = 0;
        EdgeType prevEdgeType = get(1).getEdgeType();

        for (int i = 2; i < path.size(); i++) {
            MetroNodeWithEdge node = path.get(i);
            EdgeType currEdgeType = node.getEdgeType();
            if (EdgeType.checkLineTrf(prevEdgeType, currEdgeType) || EdgeType.checkGenExpTrf(prevEdgeType, currEdgeType)) {
                cnt++;
            }
            prevEdgeType = currEdgeType;
        }

        return cnt;
    }

    public int size() {
        return path.size();
    }

    public MetroNodeWithEdge get(int idx) {
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
                get(i).setEdgeType(EdgeType.GEN_EDGE);
                get(i + 1).setWaitingTime(0);
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
    }

    /**
     * 압축된 경로에서는 MSI만 유효함 (weight, edgeType, waitingTime 의미 X)
     */
    public MetroPath getCompressedPath() {
        boolean[] check = new boolean[size()];  // compressedPath에 포함할 요소인지 판정
        check[0] = true;
        check[size() - 1] = true;

        boolean recentExpState = false;
        for (int i = 0; i < size(); i++) {
            MetroNodeWithEdge curr = get(i);

            // TODO. 분기점 검사
            if (curr.isJctStin()) {
                check[i] = true;
            }

            // TODO. 환승역 여부 검사
            if (curr.getEdgeType() == EdgeType.TRF_EDGE) {
                recentExpState = false;
                check[i - 1] = true;
                check[i] = true;
            }

            // TODO. 급행 정차역 여부 검사
            if (curr.isExpStin() == recentExpState) {
                continue;
            }
            if (curr.isExpStin()) {
                check[i] = true;
                recentExpState = true;
                continue;
            }
            check[i - 1] = true;
            recentExpState = false;
        }

        MetroPath compressedPath = new MetroPath(new ArrayList<>());
        for (int i = 0; i < check.length; i++) {
            if (check[i]) {
                compressedPath.addNode(new MetroNodeWithEdge(get(i)));
            }
        }

        return compressedPath;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (MetroNodeWithEdge node : path) {
            sb.append(node).append('\t');
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
        return Double.compare(this.getTotalWeight(), o.getTotalWeight());
    }
}

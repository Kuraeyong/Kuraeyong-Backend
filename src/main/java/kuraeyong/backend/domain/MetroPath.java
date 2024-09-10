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

    public double getPathWeight() {
        double sum = 0;
        for (MetroNodeWithEdge node : path) {
            sum += node.getWeight();
        }
        return Math.round(sum * 10) / 10.0;
    }

    public int getTrfCnt() {
        int cnt = 0;
        String lnCd = get(0).getLnCd();

        for (MetroNodeWithEdge node : path) {
            if (lnCd.equals(node.getLnCd())) {
                continue;
            }
            cnt++;
            lnCd = node.getLnCd();
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
     * 압축된 경로에서는 더 이상 weight가 유효하지 않음
     */
    public MetroPath getCompressedPath() {
        MetroPath compressedPath = new MetroPath(new ArrayList<>());

        compressedPath.addNode(new MetroNodeWithEdge(get(0)));    // 출발 노드
        for (int i = 1; i < size() - 1; i++) {
            MetroNodeWithEdge prev = get(i - 1);
            MetroNodeWithEdge curr = get(i);

            if (!prev.getLnCd().equals(curr.getLnCd())) {   // 환승역인 경우 (노선이 변경된 경우)
                if (!prev.isJctStin()) {
                    compressedPath.addNode(new MetroNodeWithEdge(prev));
                }
                compressedPath.addNode(new MetroNodeWithEdge(curr));
                continue;
            }
            if (curr.isJctStin()) { // 분기점인 경우
                // 지선 환승은 현재 단계에서 고려 X (실제 시간표 조회에서 고려)
                compressedPath.addNode(new MetroNodeWithEdge(curr));
            }
        }
        compressedPath.addNode(new MetroNodeWithEdge(get(size() - 1)));   // 종료 노드

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
        return Double.compare(this.getPathWeight(), o.getPathWeight());
    }
}

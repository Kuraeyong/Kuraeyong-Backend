package kuraeyong.backend.domain.path;

import kuraeyong.backend.domain.constant.BranchDirectionType;
import kuraeyong.backend.domain.constant.DirectionType;
import kuraeyong.backend.domain.constant.EdgeType;
import kuraeyong.backend.domain.station.info.MinimumStationInfo;
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

    public void concat(MetroPath path, boolean usedInYen) {    // 깊은 복사
        int idx = usedInYen ? 1 : 0;
        for (MetroNodeWithEdge node : path.subPath(idx, path.size()).getPath()) {
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

    public int size() {
        return path.size();
    }

    public MetroNodeWithEdge get(int idx) {
        return path.get(idx);
    }

    public MetroNodeWithEdge getFromEnd(int idxFromEnd) {
        return get(size() - idxFromEnd);
    }

    public String getStinNm(int idx) {
        return path.get(idx).getStinNm();
    }

    public MetroPath subPath(int start, int end) {
        return new MetroPath(path.subList(start, end));
    }

    /**
     * 출발역->출발역 및 도착역->도착역인 불필요한 경로 제거
     */
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
    public MetroPath createCompressedPath() {
        boolean[] check = new boolean[size()];  // compressedPath에 포함할 요소인지 판정
        check[0] = true;
        check[size() - 1] = true;

        boolean recentExpState = false;
        for (int i = 0; i < size(); i++) {
            MetroNodeWithEdge curr = get(i);

            // 분기점 검사
            if (curr.isJctStin()) {
                check[i] = true;
            }

            // 환승역 여부 검사
            if (curr.getEdgeType() == EdgeType.TRF_EDGE) {
                recentExpState = false;
                check[i - 1] = true;
                check[i] = true;
            }

            // 급행 정차역 여부 검사
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

        int idx = 1;
        while (idx != -1) {
            idx = compressedPath.addBranchTrfNode(idx);
        }
        compressedPath.setDirection();
        return compressedPath;
    }

    /**
     * 경로를 순회하면서 분기점 환승이 필요한 역을 발견한 경우, 경로에 분기점 환승을 나타내는 MetroNodeWithEdge 하나를 추가
     *
     * @param idx 분기점 환승 검사를 시작할 노드의 인덱스
     * @return 분기점 환승 검사를 시작할 다음 노드의 인덱스를 반환
     */
    private int addBranchTrfNode(int idx) {
        for (int i = idx; i < size() - 1; i++) {
            MetroNodeWithEdge prev = get(i - 1);
            MetroNodeWithEdge curr = get(i);
            MetroNodeWithEdge next = get(i + 1);
            String prevBranchInfo = prev.getBranchInfo();
            String nextBranchInfo = next.getBranchInfo();

            if (!BranchDirectionType.isBranchTrf(prev, curr, next)) {
                continue;
            }
            path.add(i + 1, MetroNodeWithEdge.builder()
                    .node(curr.getNode())
                    .edgeType(EdgeType.TRF_EDGE)
                    .branchDirection(BranchDirectionType.convertToBranchDirectionType(prevBranchInfo, nextBranchInfo))
                    .build());
            return i + 2;
        }
        return -1;
    }

    public void setDirection() {
        // 일반, 급행 간선의 방향 설정 (상, 하)
        for (int i = 0; i < size() - 1; i++) {
            MetroNodeWithEdge curr = get(i);
            MetroNodeWithEdge next = get(i + 1);
            MinimumStationInfo nextMSI = MinimumStationInfo.get(next);

            if (next.getEdgeType() == EdgeType.TRF_EDGE) {
                continue;
            }
            if (nextMSI.isEungam() && !curr.getBranchInfo().equals("0")) {  // 6호선 순환구간 처리
                next.setDirection(DirectionType.DOWN);
                continue;
            }
            if (curr.getUpDownOrder() > next.getUpDownOrder()) {
                next.setDirection(DirectionType.UP);
                continue;
            }
            next.setDirection(DirectionType.DOWN);
        }
        get(0).setDirection(get(1).getDirection());

        // 환승 간선의 방향 설정 (상상, 상하, 하상, 하하)
        for (int i = 1; i < size() - 1; i++) {
            MetroNodeWithEdge prev = get(i - 1);
            MetroNodeWithEdge curr = get(i);
            MetroNodeWithEdge next = get(i + 1);

            if (curr.getEdgeType() != EdgeType.TRF_EDGE) {
                continue;
            }
            curr.setDirection(DirectionType.convertToTrfDirectionType(prev.getDirection(), next.getDirection()));
        }
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

package kuraeyong.backend.manager;

import kuraeyong.backend.common.exception.ErrorMessage;
import kuraeyong.backend.common.exception.PathSearchResultException;
import kuraeyong.backend.domain.constant.DomainType;
import kuraeyong.backend.domain.constant.EdgeType;
import kuraeyong.backend.domain.graph.GraphForPathSearch;
import kuraeyong.backend.domain.graph.MetroEdge;
import kuraeyong.backend.domain.graph.MetroNode;
import kuraeyong.backend.domain.path.MetroNodeWithEdge;
import kuraeyong.backend.domain.path.MetroPath;
import kuraeyong.backend.domain.station.info.MinimumStationInfo;
import kuraeyong.backend.domain.station.info.MinimumStationInfoWithDateType;
import kuraeyong.backend.domain.station.time_table.StationTimeTableMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

@Component
@RequiredArgsConstructor
public class TemporaryPathsManager {
    private final GraphForPathSearch graphForPathSearch;
    private final StationTimeTableMap stationTimeTableMap;

    private static final int YEN_CANDIDATE_CNT = 10;

    /**
     * 옌 알고리즘을 통해 임시 경로 목록을 생성
     *
     * @param orgNo      출발역 번호
     * @param destNo     도착역 번호
     * @param containExp 급행 간선 포함 여부
     * @param dateType   요일 종류
     * @return 임시 경로 목록
     */
    public List<MetroPath> create(int orgNo, int destNo, boolean containExp, String dateType) {
        List<MetroPath> temporaryPathCandidates = new ArrayList<>();
        Set<MetroPath> uniqueTemporaryPathCandidates = new HashSet<>();
        PriorityQueue<MetroPath> sortedTemporaryPathCandidates = new PriorityQueue<>();

        // 첫 번째 최단 경로 계산
        MetroPath initialPath = createTemporaryPathCandidate(orgNo, destNo, null, containExp, dateType);
        if (initialPath == null) {
            throw new PathSearchResultException(ErrorMessage.TEMPORARY_PATH_NOT_FOUND);
        }
        temporaryPathCandidates.add(initialPath);
        uniqueTemporaryPathCandidates.add(initialPath);

        // 남은 (YEN_CANDIDATE_CNT-1)개의 경로를 탐색
        for (int i = 1; i < YEN_CANDIDATE_CNT; i++) {
            MetroPath prevPath = temporaryPathCandidates.get(i - 1);   // 이전에 찾은 최단 경로

            // 각 스퍼 노드에 대해 새로운 경로를 탐색
            for (int j = 0; j < prevPath.size() - 1; j++) {
                MetroNodeWithEdge spurNode = prevPath.get(j);
                MetroPath rootPath = prevPath.subPath(0, j + 1); // 루트 경로 계산

                // 루트 경로와 중복되지 않도록 기존 경로에서 간선을 제거
                List<MetroEdge> removedEdgeList = new ArrayList<>();
                for (MetroPath temporaryPathCandidate : temporaryPathCandidates) {
                    if (temporaryPathCandidate.size() > j + 1 && temporaryPathCandidate.subPath(0, j + 1).equals(rootPath)) {
                        MetroNode orgNode = graphForPathSearch.get(temporaryPathCandidate.get(j).getNodeNo());
                        MetroNode destNode = graphForPathSearch.get(temporaryPathCandidate.get(j + 1).getNodeNo());
                        MetroEdge removedGeneralEdge = graphForPathSearch.removeEdge(orgNode, destNode, EdgeType.GEN_EDGE);
                        MetroEdge removedExpressEdge = graphForPathSearch.removeEdge(orgNode, destNode, EdgeType.EXP_EDGE);
                        MetroEdge removedTransferEdge = graphForPathSearch.removeEdge(orgNode, destNode, EdgeType.TRF_EDGE);

                        if (removedGeneralEdge != null) {
                            removedEdgeList.add(removedGeneralEdge);
                        }
                        if (removedExpressEdge != null) {
                            removedEdgeList.add(removedExpressEdge);
                        }
                        if (removedTransferEdge != null) {
                            removedEdgeList.add(removedTransferEdge);
                        }
                    }
                }

                MetroPath spurPath = createTemporaryPathCandidate(spurNode.getNodeNo(), destNo, rootPath, containExp, dateType);
                if (spurPath != null) {
                    MetroPath totalPath = new MetroPath(rootPath);
                    totalPath.concat(spurPath, true);
                    if (!uniqueTemporaryPathCandidates.contains(totalPath)) {
                        sortedTemporaryPathCandidates.add(totalPath);
                        uniqueTemporaryPathCandidates.add(totalPath);
                    }
                }

                // 제거된 간선을 복원
                for (MetroEdge edge : removedEdgeList) {
                    graphForPathSearch.addEdge(spurNode.getNode(), edge);
                }
            }

            // 후보 경로 중 최단 경로를 선택하여, 최단 경로 리스트에 추가
            if (sortedTemporaryPathCandidates.isEmpty()) {
                break;
            }
            temporaryPathCandidates.add(sortedTemporaryPathCandidates.poll());
        }
        return selectUpperRankedCandidates(temporaryPathCandidates, sortedTemporaryPathCandidates);
    }

    /**
     * 다익스트라 알고리즘을 통해, 하나의 임시 경로 후보를 생성
     *
     * @param orgNo      출발역 번호
     * @param destNo     도착역 번호
     * @param rootPath   현재 생성하려는 간이 경로가 스퍼 경로인 경우에 필요한 루트 경로 정보
     * @param containExp 급행 간선 포함 여부
     * @param dateType   요일 종류
     * @return 임시 경로 후보
     */
    private MetroPath createTemporaryPathCandidate(int orgNo, int destNo, MetroPath rootPath, boolean containExp, String dateType) {
        int graphSize = graphForPathSearch.size();
        boolean[] check = new boolean[graphSize];
        double[] dist = new double[graphSize];
        MetroNodeWithEdge[] prevNode = new MetroNodeWithEdge[graphSize];

        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[orgNo] = 0;

        // 경춘선 광운대 운행 X
        dist[graphForPathSearch.getNodeFromMetroMap(StationTimeTableMap.K2_KWANGWOON).getNodeNo()] = -1;

        // rootPath를 기반으로 check 초기화
        if (rootPath != null) {
            for (MetroNodeWithEdge node : rootPath.getPath()) {
                check[node.getNodeNo()] = true;
            }
            check[orgNo] = false;

            // rootPath 기반으로 출발역(org)의 prevNode 설정
            MetroNodeWithEdge lastNodeOfRootPath = rootPath.get(rootPath.size() - 1);
            if (rootPath.size() >= 2) {
                prevNode[orgNo] = MetroNodeWithEdge.builder()
                        .node(rootPath.get(rootPath.size() - 2).getNode())
                        .weight(lastNodeOfRootPath.getWeight())
                        .edgeType(lastNodeOfRootPath.getEdgeType())
                        .waitingTime(lastNodeOfRootPath.getWaitingTime())
                        .build();
            }
        }

        PriorityQueue<MetroNodeWithEdge> pq = new PriorityQueue<>();
        pq.offer(MetroNodeWithEdge.builder()
                .node(graphForPathSearch.get(orgNo))
                .weight(0)
                .build());    // (1, 0)

        while (!pq.isEmpty()) {
            MetroNode now = pq.poll().getNode();    // 1
            int nowNo = now.getNodeNo();

            if (check[nowNo]) {
                continue;
            }
            check[nowNo] = true;

            for (MetroEdge edge : now.getEdgeList()) {  // e.g.) nowNo = 1, edgeList=[(2,10,0), (4,25,1), (3,40,0)]
                if (!containExp && edge.isExpEdge()) {
                    continue;
                }
                double weight = edge.getWeight();   // 10
                double waitingTime = 0;
                if (prevNode[nowNo] != null) {
                    EdgeType prevEdgeType = prevNode[nowNo].getEdgeType();
                    EdgeType currEdgeType = edge.getEdgeType();  // 0
                    if (EdgeType.checkLineTrf(prevEdgeType, currEdgeType) || EdgeType.checkGenExpTrf(prevEdgeType, currEdgeType)) {
                        MinimumStationInfo minimumStationInfo = MinimumStationInfo.build(now.getRailOprIsttCd(), now.getLnCd(), now.getStinCd());
                        MinimumStationInfoWithDateType key = new MinimumStationInfoWithDateType(minimumStationInfo, dateType, DomainType.STATION_TIME_TABLE);
                        waitingTime = stationTimeTableMap.getAvgWaitingTime(key);
                    }
                }

                if (dist[edge.getTrfNodeNo()] > dist[nowNo] + weight + waitingTime) { // dist[2] > dist[1] + 10
                    dist[edge.getTrfNodeNo()] = dist[nowNo] + weight + waitingTime;
                    prevNode[edge.getTrfNodeNo()] = MetroNodeWithEdge.builder()
                            .node(now)
                            .weight(weight)
                            .edgeType(edge.getEdgeType())
                            .waitingTime(waitingTime)
                            .build();   // prevNode[2] = (1, 10, 0)
                    pq.offer(MetroNodeWithEdge.builder()
                            .node(graphForPathSearch.get(edge.getTrfNodeNo()))
                            .weight(dist[edge.getTrfNodeNo()])
                            .build());  // (2, 10)
                }
            }
        }

        if (dist[destNo] == Integer.MAX_VALUE) {
            return null;
        }
        return interpretResultByTracking(prevNode, orgNo, destNo);
    }

    /**
     * 다익스트라 알고리즘에서 나온 PrevNode를 역추적함에 따라, 임시 경로 후보를 생성
     *
     * @param prevNode 이전 노드들
     * @param orgNo    출발역 번호
     * @param destNo   도착역 번호
     * @return 임시 경로 후보
     */
    private MetroPath interpretResultByTracking(MetroNodeWithEdge[] prevNode, int orgNo, int destNo) {
        Stack<MetroNodeWithEdge> pathStack = new Stack<>();

        // push
        while (destNo != orgNo) {
            MetroNodeWithEdge node = prevNode[destNo];
            pathStack.push(MetroNodeWithEdge.builder()
                    .node(graphForPathSearch.get(destNo))
                    .weight(node.getWeight())
                    .edgeType(node.getEdgeType())
                    .waitingTime(node.getWaitingTime())
                    .build());    // (5, 20, 1)
            destNo = node.getNodeNo();
        }
        pathStack.push(MetroNodeWithEdge.builder()
                .node(graphForPathSearch.get(orgNo))
                .weight(0)
                .edgeType(EdgeType.NONE)
                .waitingTime(0)
                .build());    // (1, 0, -1)

        // pop
        MetroPath path = new MetroPath(new ArrayList<>());
        while (!pathStack.isEmpty()) {
            path.addNode(pathStack.pop());
        }

        return path;
    }

    /**
     * 임시 경로 후보 목록에서 상위 {YEN_CANDIDATE_CNT}개의 임시 경로 후보들을 선정
     *
     * @param temporaryPathCandidates       임시 경로 후보 목록
     * @param sortedTemporaryPathCandidates 우선순위에 따라 정렬된 임시 경로 후보 목록
     * @return 상위 랭크의 임시 경로 후보 목록
     */
    private List<MetroPath> selectUpperRankedCandidates(List<MetroPath> temporaryPathCandidates, PriorityQueue<MetroPath> sortedTemporaryPathCandidates) {
        // 불필요한 경로 제거 후, 중복을 제거하기 위해 pathSet에 모두 집합
        Set<MetroPath> uniqueTemporaryPathCandidates = new HashSet<>();
        for (MetroPath temporaryPathCandidate : temporaryPathCandidates) {
            temporaryPathCandidate.removeUnnecessaryPath();
            uniqueTemporaryPathCandidates.add(temporaryPathCandidate);
        }
        for (MetroPath sortedTemporaryPathCandidate : sortedTemporaryPathCandidates) {
            sortedTemporaryPathCandidate.removeUnnecessaryPath();
            uniqueTemporaryPathCandidates.add(sortedTemporaryPathCandidate);
        }

        // 정렬을 위해 우선순위 큐에 삽입
        sortedTemporaryPathCandidates.clear();
        sortedTemporaryPathCandidates.addAll(uniqueTemporaryPathCandidates);

        // 상위 k개에 대해서 조회
        List<MetroPath> temporaryPaths = new ArrayList<>();
        while (!sortedTemporaryPathCandidates.isEmpty() && temporaryPaths.size() < YEN_CANDIDATE_CNT) {
            MetroPath sortedTemporaryPathCandidate = sortedTemporaryPathCandidates.poll();
            if (isEfficientTemporaryPath(sortedTemporaryPathCandidate)) {
                temporaryPaths.add(sortedTemporaryPathCandidate);
            }
        }
        return temporaryPaths;
    }

    /**
     * 효율적인 임시 경로 여부를 반환
     *
     * @param temporaryPath 임시 경로
     * @return 효율적인 임시 경로 여부
     */
    private boolean isEfficientTemporaryPath(MetroPath temporaryPath) {
        HashMap<String, Integer> firstOccurrenceIdx = new HashMap<>();

        for (int idx = 0; idx < temporaryPath.size(); idx++) {
            String stinNm = temporaryPath.get(idx).getStinNm();
            if (!firstOccurrenceIdx.containsKey(stinNm)) {  // 처음 등장하는 역명은 맵에 추가
                firstOccurrenceIdx.put(stinNm, idx);
                continue;
            }
            if (firstOccurrenceIdx.get(stinNm) + 1 != idx) {    // 동일 역명이 연속된 경로가 아니라면
                return false;
            }
        }
        return true;
    }
}
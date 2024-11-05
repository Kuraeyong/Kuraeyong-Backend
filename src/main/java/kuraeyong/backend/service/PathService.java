package kuraeyong.backend.service;

import kuraeyong.backend.domain.constant.DomainType;
import kuraeyong.backend.domain.constant.EdgeType;
import kuraeyong.backend.domain.constant.SortType;
import kuraeyong.backend.domain.graph.GraphForPathSearch;
import kuraeyong.backend.domain.graph.MetroEdge;
import kuraeyong.backend.domain.graph.MetroNode;
import kuraeyong.backend.domain.path.MetroNodeWithEdge;
import kuraeyong.backend.domain.path.MetroPath;
import kuraeyong.backend.domain.path.MoveInfoList;
import kuraeyong.backend.domain.path.PathResult;
import kuraeyong.backend.domain.path.PathResults;
import kuraeyong.backend.domain.station.congestion.StationCongestionMap;
import kuraeyong.backend.domain.station.info.MinimumStationInfo;
import kuraeyong.backend.domain.station.info.MinimumStationInfoWithDateType;
import kuraeyong.backend.domain.station.time_table.StationTimeTableMap;
import kuraeyong.backend.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

@Slf4j
@Service
@RequiredArgsConstructor
public class PathService {

    private final GraphForPathSearch graphForPathSearch;
    private final StationService stationService;
    private final MoveService moveService;
    private final StationTimeTableMap stationTimeTableMap;
    private final StationCongestionMap stationCongestionMap;
    private static final int YEN_CANDIDATE_CNT = 10;

    public String searchPath(String orgStinNm, String destStinNm, String dateType, int hour, int min, int congestionThreshold, String convenience) {
        validateExistStinNm(orgStinNm);
        validateExistStinNm(destStinNm);

        List<MetroPath> temporaryPaths = createTemporaryPaths(orgStinNm, destStinNm, dateType);
        PathResults pathResults = createPathResults(temporaryPaths, dateType, hour, min);
        stationCongestionMap.setCongestionScoreOfPaths(pathResults, dateType, congestionThreshold);
        pathResults.sort(SortType.CONGESTION);
        showPathResults(pathResults);

        return "successfully searched";
    }

    /**
     * 출발역, 도착역, 요일만을 고려한 간이 경로 목록 생성
     *
     * @param orgStinNm  출발역명
     * @param destStinNm 도착역명
     * @param dateType   요일 정보
     * @return 간이 경로 목록
     */
    private List<MetroPath> createTemporaryPaths(String orgStinNm, String destStinNm, String dateType) {
        // 경로 탐색용 그래프 초기화
        graphForPathSearch.init();
        MinimumStationInfo orgMSI = stationService.getStationByName(orgStinNm);
        MinimumStationInfo destMSI = stationService.getStationByName(destStinNm);
        int orgNo = graphForPathSearch.addNode(orgMSI);
        int destNo = graphForPathSearch.addNode(destMSI);
        graphForPathSearch.updateEdgeList(orgNo);
        graphForPathSearch.updateEdgeList(destNo);

        // 간이 경로 리스트 생성
        List<MetroPath> temporaryPaths = searchCandidatePathList(orgNo, destNo, false, dateType);
        assert temporaryPaths != null;
        List<MetroPath> temporaryPathsWithExpEdge = searchCandidatePathList(orgNo, destNo, true, dateType);
        if (temporaryPathsWithExpEdge != null) {
            temporaryPaths.addAll(temporaryPathsWithExpEdge);
        }
        temporaryPaths = removeDuplicatedElement(temporaryPaths);
        addDirectPath(temporaryPaths, orgNo, destNo);
        return temporaryPaths;
    }

    /**
     * 간이 경로 목록과 시간 정보를 이용해 실제 경로 목록을 생성
     *
     * @param temporaryPaths 간이 경로 목록
     * @param dateType       요일 정보
     * @param hour           시간
     * @param min            분
     * @return 실제 경로 목록
     */
    private PathResults createPathResults(List<MetroPath> temporaryPaths, String dateType, int hour, int min) {
        PathResults pathResults = new PathResults();
        for (MetroPath path : temporaryPaths) {
            path.setDirection();
            MetroPath compressedPath = path.getCompressPath();
            MoveInfoList moveInfoList = moveService.createMoveInfoList(compressedPath, dateType, hour, min);
            if (moveInfoList == null) {
                continue;
            }
            setPassingTimeOfPath(path, compressedPath);
            pathResults.add(new PathResult(path, moveInfoList));
        }
        return pathResults;
    }

    /**
     * 경로 탐색 결과를 출력
     *
     * @param pathResults 경로 탐색 결과
     */
    private void showPathResults(PathResults pathResults) {
        if (pathResults.isEmpty()) {
            System.out.println("현재 운행중인 열차가 없습니다.");
        }
        System.out.print(pathResults);
    }

    /**
     * 압축 경로 경유역의 passingTime을 기반으로, 일반 경로 모든 역의 passingTime을 설정
     *
     * @param path           일반 경로
     * @param compressedPath 압축 경로
     */
    private void setPassingTimeOfPath(MetroPath path, MetroPath compressedPath) {
        // 압축 경로 경유역의 passingTime을 일반 경로의 해당역에도 반영
        for (MetroNodeWithEdge compressedPathNode : compressedPath.getPath()) {
            for (MetroNodeWithEdge node : path.getPath()) {
                if (!MinimumStationInfo.get(node).equals(MinimumStationInfo.get(compressedPathNode))) {
                    continue;
                }
                node.setPassingTime(compressedPathNode.getPassingTime());
            }
        }

        // 일반 경로 모든역의 passingTime 설정
        int size = path.size();
        int lastPassingTimeIdx = 0;
        for (int i = 1; i < size; i++) {
            MetroNodeWithEdge curr = path.get(i);
            if (curr.getPassingTime() == null) {
                continue;
            }
            // i가 압축 경로 경유역인 경우
            int increment = DateUtil.getMinDiff(path.get(lastPassingTimeIdx).getPassingTime(), curr.getPassingTime()) / (i - lastPassingTimeIdx);
            for (int j = lastPassingTimeIdx + 1, incrementCnt = 1; j < i; j++) {
                path.get(j).setPassingTime(DateUtil.plusMinutes(path.get(lastPassingTimeIdx).getPassingTime(), increment * incrementCnt++));
            }
            lastPassingTimeIdx = i;
        }

        // 압축 경로의 마지막 경유역 처리
        MetroNodeWithEdge lastPassingTimeNode = path.get(lastPassingTimeIdx);
        MetroNodeWithEdge lastNode = path.get(size - 1);
        int increment = DateUtil.getMinDiff(lastPassingTimeNode.getPassingTime(), lastNode.getPassingTime()) / (size - lastPassingTimeIdx);
        for (int j = lastPassingTimeIdx + 1, incrementCnt = 1; j < size; j++) {
            path.get(j).setPassingTime(DateUtil.plusMinutes(lastPassingTimeNode.getPassingTime(), increment * incrementCnt++));
        }
    }

    /**
     * 중복된 간이 경로를 제거
     *
     * @param temporaryPaths 간이 경로 목록
     * @return 간이 경로들이 중복되지 않는, 간이 경로 목록
     */
    private List<MetroPath> removeDuplicatedElement(List<MetroPath> temporaryPaths) {
        LinkedHashSet<MetroPath> linkedHashSet = new LinkedHashSet<>(temporaryPaths);

        return new ArrayList<>(linkedHashSet);
    }

    /**
     * 두 일반역을 직선으로 잇는 경로를 추가
     *
     * @param temporaryPaths 간이 경로 목록
     * @param orgNo          출발역명
     * @param destNo         도착역명
     */
    private void addDirectPath(List<MetroPath> temporaryPaths, int orgNo, int destNo) {
        // 두 일반역을 직선으로 잇는 경로를 추가할 필요가 없는지 검사
        MetroNode org = graphForPathSearch.get(orgNo);
        MetroNode dest = graphForPathSearch.get(destNo);
        if (!org.getLnCd().equals(dest.getLnCd())) {    // 노선 환승이 필요한 경우
            return;
        }
        if (orgNo < graphForPathSearch.getTrfOrExpStinCnt() || destNo < graphForPathSearch.getTrfOrExpStinCnt()) {    // 하나라도 일반역이 아닌 경우
            return;
        }

        // 두 일반역을 직선으로 잇는 경로 하나 추가
        MetroPath directPath = new MetroPath(new ArrayList<>());
        directPath.addNode(MetroNodeWithEdge.builder()
                .node(new MetroNode(org))
                .edgeType(EdgeType.GEN_EDGE)
                .build());
        directPath.addNode(MetroNodeWithEdge.builder()
                .node(new MetroNode(dest))
                .edgeType(EdgeType.GEN_EDGE)
                .build());
        temporaryPaths.add(directPath);
    }

    /**
     * 다익스트라 알고리즘을 통해 하나의 간이 경로를 생성
     *
     * @param orgNo      출발역 번호
     * @param destNo     도착역 번호
     * @param rootPath   현재 생성하려는 간이 경로가 스퍼 경로인 경우에 필요한 루트 경로 정보
     * @param containExp 급행 간선 포함 여부
     * @param dateType   요일 종류
     * @return 간이 경로
     */
    private MetroPath createTemporaryPath(int orgNo, int destNo, MetroPath rootPath, boolean containExp, String dateType) {
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
        return createPath(prevNode, orgNo, destNo);
    }

    /**
     * 옌 알고리즘을 통해서 여러 간이 경로를 탐색
     */
    private List<MetroPath> searchCandidatePathList(int orgNo, int destNo, boolean containExp, String dateType) {
        List<MetroPath> shortestPathList = new ArrayList<>();
        Set<MetroPath> pathSet = new HashSet<>();

        // 첫 번째 최단 경로 계산
        MetroPath initialPath = createTemporaryPath(orgNo, destNo, null, containExp, dateType);
        if (initialPath == null) {
            return null;
        }
        shortestPathList.add(initialPath);
        pathSet.add(initialPath);

        // 후보 경로들을 저장할 우선순위 큐
        PriorityQueue<MetroPath> candidates = new PriorityQueue<>();

        // 남은 (YEN_CANDIDATE_CNT-1)개의 경로를 탐색
        for (int i = 1; i < YEN_CANDIDATE_CNT; i++) {
            MetroPath prevPath = shortestPathList.get(i - 1);   // 이전에 찾은 최단 경로

            // 각 스퍼 노드에 대해 새로운 경로를 탐색
            for (int j = 0; j < prevPath.size() - 1; j++) {
                MetroNodeWithEdge spurNode = prevPath.get(j);
                MetroPath rootPath = prevPath.subPath(0, j + 1); // 루트 경로 계산

                // 루트 경로와 중복되지 않도록 기존 경로에서 간선을 제거
                List<MetroEdge> removedEdgeList = new ArrayList<>();
                for (MetroPath path : shortestPathList) {
                    if (path.size() > j + 1 && path.subPath(0, j + 1).equals(rootPath)) {
                        MetroNode orgNode = graphForPathSearch.get(path.get(j).getNodeNo());
                        MetroNode destNode = graphForPathSearch.get(path.get(j + 1).getNodeNo());
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

                MetroPath spurPath = createTemporaryPath(spurNode.getNodeNo(), destNo, rootPath, containExp, dateType);
                if (spurPath != null) {
                    MetroPath totalPath = new MetroPath(rootPath);
                    totalPath.concat(spurPath);
                    if (!pathSet.contains(totalPath)) {
                        candidates.add(totalPath);
                        pathSet.add(totalPath);
                    }
                }

                // 제거된 간선을 복원
                for (MetroEdge edge : removedEdgeList) {
                    graphForPathSearch.addEdge(spurNode.getNode(), edge);
                }
            }

            // 후보 경로 중 최단 경로를 선택하여, 최단 경로 리스트에 추가
            if (candidates.isEmpty()) {
                break;
            }
            shortestPathList.add(candidates.poll());
        }
        removeUnnecessaryPath(shortestPathList, candidates);

        return shortestPathList;
    }

    /**
     * 출발역->출발역 및 도착역->도착역인 불필요한 경로 제거
     *
     * @param shortestPaths 최단 경로 목록
     * @param candidates    후보 경로 목록
     */
    private void removeUnnecessaryPath(List<MetroPath> shortestPaths, PriorityQueue<MetroPath> candidates) {
        // 불필요한 경로 제거 후, 중복을 제거하기 위해 pathSet에 모두 집합
        Set<MetroPath> paths = new HashSet<>();
        for (MetroPath shortestPath : shortestPaths) {
            shortestPath.removeUnnecessaryPath();
            paths.add(shortestPath);
        }
        for (MetroPath candidate : candidates) {
            candidate.removeUnnecessaryPath();
            paths.add(candidate);
        }

        // 정렬을 위해 우선순위 큐에 삽입
        candidates.clear();
        candidates.addAll(paths);

        // 상위 k개에 대해서 조회
        shortestPaths.clear();
        while (!candidates.isEmpty() && shortestPaths.size() < YEN_CANDIDATE_CNT) {
            MetroPath candidate = candidates.poll();
            if (isEfficientPath(candidate)) {
                shortestPaths.add(candidate);
            }
        }
    }

    /**
     * 공딴딴공을 포함한 경로인지 검사
     *
     * @param candidate 후보 경로
     * @return 공딴딴공 포함 경로 여부
     */
    private boolean isEfficientPath(MetroPath candidate) {
        HashMap<String, Integer> firstOccurrenceIdx = new HashMap<>();

        for (int idx = 0; idx < candidate.size(); idx++) {
            String stinNm = candidate.get(idx).getStinNm();
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

    /**
     * 다익스트라 알고리즘의 결과로, 간이 경로를 생성
     *
     * @param prevNode 이전 노드들
     * @param orgNo    출발역 번호
     * @param destNo   도착역 번호
     * @return 간이 경로
     */
    private MetroPath createPath(MetroNodeWithEdge[] prevNode, int orgNo, int destNo) {
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

    private void validateExistStinNm(String stinNm) {
        if (stationService.getStationByName(stinNm) == null) {
            throw new IllegalArgumentException("존재하지 않는 역명입니다.");
        }
    }
}

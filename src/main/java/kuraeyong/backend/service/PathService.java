

package kuraeyong.backend.service;

import kuraeyong.backend.domain.*;
import kuraeyong.backend.dto.MinimumStationInfo;
import kuraeyong.backend.dto.MinimumStationInfoWithDateType;
import kuraeyong.backend.dto.MoveInfo;
import kuraeyong.backend.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PathService {

    private final GraphForPathSearch graphForPathSearch;
    private final StationService stationService;
    private final StationTimeTableMap stationTimeTableMap;
    private final StationTrfWeightMap stationTrfWeightMap;
    private final static int YEN_CANDIDATE_CNT = 10;
    private final static int TRAIN_CANDIDATE_CNT = 3;

    public String searchPath(String orgStinNm, String destStinNm, String dateType, int hour, int min) {
        // TODO 1. 시간표 API를 조회할 간이 경로 조회
        graphForPathSearch.init();
        MinimumStationInfo orgStin = stationService.getStationByName(orgStinNm);
        MinimumStationInfo destStin = stationService.getStationByName(destStinNm);
        int orgNo = graphForPathSearch.addNode(orgStin);
        int destNo = graphForPathSearch.addNode(destStin);
        graphForPathSearch.updateEdgeList(orgNo);
        graphForPathSearch.updateEdgeList(destNo);

        List<MetroPath> shortestPathList = searchCandidatePathList(orgNo, destNo, false, dateType);
        if (shortestPathList == null) {
            // FIXME: 간이 경로를 하나도 못 찾은 경우
            return "not existed";
        }
        List<MetroPath> shortestPathListWithExpEdge = searchCandidatePathList(orgNo, destNo, true, dateType);
        if (shortestPathListWithExpEdge != null) {
            shortestPathList.addAll(shortestPathListWithExpEdge);
        }
        shortestPathList = removeDuplicatedElement(shortestPathList);

        // TODO 2. 간이 경로와 시간표 API를 이용해 실소요시간을 포함한 이동 정보 조회
        for (MetroPath path : shortestPathList) {
            MetroPath compressedPath = path.getCompressedPath();
            compressedPath.setDirection();

            System.out.println(path);
            System.out.println(path.getTotalWeight());
            System.out.println(path.getTrfCnt());
            System.out.println(compressedPath);
            List<MoveInfo> moveInfoList = getMoveInfoList(compressedPath, dateType, hour, min);
            for (MoveInfo moveInfo : moveInfoList) {
                System.out.println(moveInfo);
            }
            System.out.println();
        }
        return "successfully searched";
    }

    private List<MetroPath> removeDuplicatedElement(List<MetroPath> shortestPathList) {
        LinkedHashSet<MetroPath> linkedHashSet = new LinkedHashSet<>(shortestPathList);

        return new ArrayList<>(linkedHashSet);
    }

    /**
     * 다익스트라 알고리즘을 통해서 하나의 간이 경로를 탐색
     *
     * @description 같은 MetroNodeWithWeight여도 다음과 같이 사용하는 방식이 조금씩 다름
     * @inline-variable prevNode         이전 노드를 담고 있는 배열
     * e.g.) [(1, 10), (2, 20), (4, 30)]
     * @inline-variable pq               경로 탐색에서 사용할 우선순위 큐
     * e.g.) [(1, 0), (2, 10), (4, 20), (5, 30)]
     */
    private MetroPath searchPath(int orgNo, int destNo, MetroPath rootPath, boolean containExp, String dateType) {
        int graphSize = graphForPathSearch.size();
        boolean[] check = new boolean[graphSize];
        double[] dist = new double[graphSize];
        MetroNodeWithEdge[] prevNode = new MetroNodeWithEdge[graphSize];

        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[orgNo] = 0;

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
                        MinimumStationInfoWithDateType key = new MinimumStationInfoWithDateType(minimumStationInfo, dateType);
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
        MetroPath initialPath = searchPath(orgNo, destNo, null, containExp, dateType);
        if (initialPath == null) {
            return null;
        }
//        initialPath.removeUnnecessaryPath();
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

                        if (removedGeneralEdge != null) {
                            removedEdgeList.add(removedGeneralEdge);
                        }
                        if (removedExpressEdge != null) {
                            removedEdgeList.add(removedExpressEdge);
                        }
                    }
                }

                MetroPath spurPath = searchPath(spurNode.getNodeNo(), destNo, rootPath, containExp, dateType);
                if (spurPath != null) {
                    MetroPath totalPath = new MetroPath(rootPath);
                    totalPath.concat(spurPath);
//                    totalPath.removeUnnecessaryPath();

                    System.out.printf("[%d, %d]\n", i, j);
                    for (int t = 0; t < shortestPathList.size(); t++) {
                        System.out.printf("shortestPath[%d]: %s\n", t, shortestPathList.get(t));
                    }
                    for (int t = 0; t < removedEdgeList.size(); t++) {
                        System.out.printf("removedEdgeList[%d]: %s\n", t, removedEdgeList.get(t));
                    }
                    System.out.printf("rootPath: %s\n", rootPath);
                    System.out.printf("spurPath: %s\n", spurPath);
                    System.out.printf("spurNode: %s\n", spurPath.get(0).getNode());
                    System.out.printf("totalPath: %s\n", totalPath);
                    System.out.printf("totalPath.getPathWeight(): %.1f\n", totalPath.getTotalWeight());
                    System.out.println();

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

        removeUnnecessaryPath(pathSet, shortestPathList, candidates);

        return shortestPathList;
    }

    /**
     * 출발역->출발역 및 도착역->도착역인 불필요한 경로 제거
     */
    private void removeUnnecessaryPath(Set<MetroPath> pathSet, List<MetroPath> shortestPathList, PriorityQueue<MetroPath> candidates) {
        // 불필요한 경로 제거 후, 중복을 제거하기 위해 pathSet에 모두 집합
        pathSet.clear();
        for (MetroPath shortestPath : shortestPathList) {
            shortestPath.removeUnnecessaryPath();
            pathSet.add(shortestPath);
        }
        for (MetroPath candidate : candidates) {
            candidate.removeUnnecessaryPath();
            pathSet.add(candidate);
        }

        // 정렬을 위해 우선순위 큐에 삽입
        candidates.clear();
        candidates.addAll(pathSet);

        // 상위 k개에 대해서 조회
        shortestPathList.clear();
        while (!candidates.isEmpty() && shortestPathList.size() < YEN_CANDIDATE_CNT) {
            MetroPath candidate = candidates.poll();
            if (isEfficientPath(candidate)) {
                shortestPathList.add(candidate);
            }
        }
    }

    /**
     * 공딴딴공 제거
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
     * 다익스트라 알고리즘의 결과로, MetroPath를 일차적으로 완성 (이후 가공)
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

    /**
     *
     * @param compressedPath    e.g.) (K4, 행신, 0.0) (K4, 홍대입구, 2.5) (2, 홍대입구, 6.5) (2, 성수, 5.5) (2, 용답, 3.5)
     * @param dateType  날짜 종류 (평일 | 토요일 | 공휴일)
     * @param hour  사용자의 해당 역 도착 시간 (시간)
     * @param min   사용자의 해당 역 도착 시간 (분)
     * @return  이동 정보 리스트
     */
    public List<MoveInfo> getMoveInfoList(MetroPath compressedPath, String dateType, int hour, int min) {
        List<MoveInfo> moveInfoList = new ArrayList<>();

        String currTime = DateUtil.getCurrTime(hour, min);
        for (int i = 0; i < compressedPath.size() - 1; i++) {
            MetroNodeWithEdge curr = compressedPath.get(i);
            MetroNodeWithEdge next = compressedPath.get(i + 1);

            MoveInfo moveInfo = getMoveInfo(curr, next, dateType, currTime);
            currTime = moveInfo.getArvTm();
            moveInfoList.add(moveInfo);
        }

        return moveInfoList;
    }

    /**
     * @param curr     현재 역 (A)
     * @param next     다음 역 (B)
     * @param dateType 날짜 유형 (평일 | 토요일 | 공휴일)
     * @param currTime 현재 시간
     * @return 이동 정보
     */
    public MoveInfo getMoveInfo(MetroNodeWithEdge curr, MetroNodeWithEdge next, String dateType, String currTime) {
        if (next.getEdgeType() == EdgeType.TRF_EDGE) {    // 환승역인 경우
            MinimumStationInfo currMSI = MinimumStationInfo.get(curr);
            MinimumStationInfo nextMSI = MinimumStationInfo.get(next);
            int weight = stationTrfWeightMap.getStationTrfWeight(currMSI, nextMSI, next.getDirection());

            return MoveInfo.builder()
                    .lnCd(null)
                    .trnNo(null)
                    .dptTm(currTime)
                    .arvTm(DateUtil.plusMinutes(currTime, weight))
                    .build();
        }

        // 현재역과 다음역을 고유하게 식별
        MinimumStationInfo A = MinimumStationInfo.get(curr);
        MinimumStationInfo B = MinimumStationInfo.get(next);

        // 현재역과 다음역의 시간표
        StationTimeTable A_TimeTable = stationTimeTableMap.get(new MinimumStationInfoWithDateType(A, dateType));
        StationTimeTable B_TimeTable = stationTimeTableMap.get(new MinimumStationInfoWithDateType(B, dateType));

        // 현재 시간 이후에 A역에 오는 열차 리스트 (이후 상시 적용)
        List<StationTimeTableElement> A_TrainList = A_TimeTable.findByDptTmGreaterThanEqual(currTime);
        if (A_TrainList == null) {
            return null;
        }

        int cnt = 0;
        StationTimeTableElement B_FastestTrain = null;   // A에서 B로 가장 빠르게 이동할 수 있는 열차 (B역 기준 시간표)
        StationTimeTableElement A_FastestTrain = null;   // A에서 B로 가장 빠르게 이동할 수 있는 열차 (A역 기준 시간표)
        for (StationTimeTableElement A_Train : A_TrainList) {
            StationTimeTableElement B_Train = B_TimeTable.getStoppingTrainAfterCurrTime(A_Train.getTrnNo(), A_Train.getDptTm());    // A에서 B로 이동할 수 있는 열차 중 하나 (B역 기준 시간표)
            if (B_Train == null) {  // 해당 열차가 B역에 정차하지 않는다면
                continue;
            }
            if (B_FastestTrain == null || B_Train.getArvTm().compareTo(B_FastestTrain.getArvTm()) <= 0) {
                B_FastestTrain = B_Train;
                A_FastestTrain = A_Train;
            }
            if (++cnt >= TRAIN_CANDIDATE_CNT) {
                break;
            }
        }

        assert A_FastestTrain != null;
        return MoveInfo.builder()
                .lnCd(A_FastestTrain.getLnCd())
                .trnNo(A_FastestTrain.getTrnNo())
                .dptTm(A_FastestTrain.getDptTm())
                .arvTm(B_FastestTrain.getArvTm())
                .build();
    }
}



package kuraeyong.backend.service;

import kuraeyong.backend.domain.*;
import kuraeyong.backend.dto.element.MinimumStationInfo;
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
    private final static int YEN_CANDIDATE_CNT = 7;

    public List<MetroPath> searchPath(String orgStinNm, String destStinNm, String dateType, int hour, int min) {
        graphForPathSearch.init();
        MinimumStationInfo orgStin = stationService.getStationByName(orgStinNm);
        MinimumStationInfo destStin = stationService.getStationByName(destStinNm);
        int orgNo = graphForPathSearch.addNode(orgStin);
        int destNo = graphForPathSearch.addNode(destStin);
        graphForPathSearch.updateEdgeList(orgNo);
        graphForPathSearch.updateEdgeList(destNo);

        return searchCandidatePathList(orgNo, destNo);
    }

    /**
     * 경로 탐색
     * 같은 MetroNodeWithWeight여도 다음과 같이 사용하는 방식이 조금씩 다름
     * ---
     * prevNode         이전 노드를 담고 있는 배열
     * e.g.) [(1, 10), (2, 20), (4, 30)]
     * pq               경로 탐색에서 사용할 우선순위 큐
     * e.g.) [(1, 0), (2, 10), (4, 20), (5, 30)]
     */
    private MetroPath searchPath(int orgNo, int destNo, MetroPath rootPath) {
        int graphSize = graphForPathSearch.size();
        boolean[] check = new boolean[graphSize];
        double[] dist = new double[graphSize];
        MetroNodeWithWeight[] prevNode = new MetroNodeWithWeight[graphSize];

        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[orgNo] = 0;

        // rootPath를 기반으로 check 초기화
        if (rootPath != null) {
            for (MetroNodeWithWeight node : rootPath.getPath()) {
                check[node.getNodeNo()] = true;
            }
            check[orgNo] = false;
        }

        PriorityQueue<MetroNodeWithWeight> pq = new PriorityQueue<>();
        pq.offer(new MetroNodeWithWeight(graphForPathSearch.get(orgNo), 0));    // (1, 0)

        while (!pq.isEmpty()) {
            MetroNode now = pq.poll().getNode();    // 1
            int nowNo = now.getNodeNo();

            if (check[nowNo]) {
                continue;
            }
            check[nowNo] = true;

            for (MetroEdge edge : now.getEdgeList()) {  // 1->2(10), (1->3(40))
                double weight = edge.getWeight();   // 10
                if (dist[edge.getTrfNodeNo()] > dist[nowNo] + weight) { // dist[2] > dist[1] + 10
                    dist[edge.getTrfNodeNo()] = dist[nowNo] + weight;
                    prevNode[edge.getTrfNodeNo()] = new MetroNodeWithWeight(now, weight);    // prevNode[2] = (1, 10)
                    pq.offer(new MetroNodeWithWeight(graphForPathSearch.get(edge.getTrfNodeNo()), dist[edge.getTrfNodeNo()]));  // (2, 10)
                }
            }
        }

        if (dist[destNo] == Integer.MAX_VALUE) {
            return null;
        }
        return createPath(prevNode, orgNo, destNo);
    }

    private List<MetroPath> searchCandidatePathList(int orgNo, int destNo) {
        List<MetroPath> shortestPathList = new ArrayList<>();
        Set<MetroPath> pathSet = new HashSet<>();

        // 첫 번째 최단 경로 계산
        MetroPath initialPath = searchPath(orgNo, destNo, null);
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
                MetroNodeWithWeight spurNode = prevPath.get(j);
                MetroPath rootPath = prevPath.subPath(0, j + 1); // 루트 경로 계산

                // 루트 경로와 중복되지 않도록 기존 경로에서 간선을 제거
                List<MetroEdge> removedEdgeList = new ArrayList<>();
                System.out.printf("[%d, %d 이전, 자르기 작업]\n", i, j);
                for (MetroPath path : shortestPathList) {
                    if (path.size() > j && path.subPath(0, j + 1).equals(rootPath)) {
                        MetroNode orgNode = graphForPathSearch.get(path.get(j).getNodeNo());
                        MetroNode destNode = graphForPathSearch.get(path.get(j + 1).getNodeNo());
                        MetroEdge removedEdge = graphForPathSearch.removeEdge(orgNode, destNode);

                        if (removedEdge != null) {
                            removedEdgeList.add(removedEdge);
                        }
                    }
                }

                MetroPath spurPath = searchPath(spurNode.getNodeNo(), destNo, rootPath);
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
//                    System.out.printf("spurNode: %s\n", graphForPathSearch.get(spurNode.getNodeNo()));
                    System.out.printf("totalPath: %s\n", totalPath);
                    System.out.printf("totalPath.getPathWeight(): %.1f\n", totalPath.getPathWeight());
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

    private MetroPath createPath(MetroNodeWithWeight[] prevNode, int orgNo, int destNo) {
        Stack<MetroNodeWithWeight> pathStack = new Stack<>();

        // push
        while (destNo != orgNo) {
            pathStack.push(new MetroNodeWithWeight(graphForPathSearch.get(destNo), prevNode[destNo].getWeight()));
            destNo = prevNode[destNo].getNodeNo();
        }
        pathStack.push(new MetroNodeWithWeight(graphForPathSearch.get(orgNo), 0));    // (1, 0)

        // pop
        MetroPath path = new MetroPath(new ArrayList<>());
        while (!pathStack.isEmpty()) {
            path.addNode(pathStack.pop());
        }
        return path;
    }

    public void printPath(MetroPath path) {
        System.out.println(path);
        System.out.println(path.getPathWeight());
    }
}

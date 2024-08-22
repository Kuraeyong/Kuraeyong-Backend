

package kuraeyong.backend.service;

import kuraeyong.backend.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PathService {

    private final GraphForPathSearch graphForPathSearch;

    public MetroPath searchPath(String orgRailOprIsttCd, String orgLnCd, String orgStinCd,
                                String destRailOprIsttCd, String destLnCd, String destStinCd) {
        graphForPathSearch.init();
        int orgNo = graphForPathSearch.addNode(orgRailOprIsttCd, orgLnCd, orgStinCd);
        int destNo = graphForPathSearch.addNode(destRailOprIsttCd, destLnCd, destStinCd);
        graphForPathSearch.updateEdgeList(orgNo);
        graphForPathSearch.updateEdgeList(destNo);

        return searchPath(orgNo, destNo, null);
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

//        printDist(dist);
        if (dist[destNo] == Integer.MAX_VALUE) {
            return null;
        }
        return createPath(prevNode, orgNo, destNo);
    }

    public List<MetroPath> searchCandidatePathList(int orgNo, int destNo, int k) {
        List<MetroPath> shortestPathList = new ArrayList<>();
        Set<MetroPath> pathSet = new HashSet<>();

        // 첫 번째 최단 경로 계산
        MetroPath initialPath = searchPath(orgNo, destNo, null);
        shortestPathList.add(initialPath);
        pathSet.add(initialPath);

        // 후보 경로들을 저장할 우선순위 큐
        PriorityQueue<MetroPath> candidates = new PriorityQueue<>();

        // 남은 (k-1)개의 경로를 탐색
        for (int i = 1; i < k; i++) {
            MetroPath prevPath = shortestPathList.get(i - 1);   // 이전에 찾은 최단 경로

            // 각 스퍼 노드에 대해 새로운 경로를 탐색
            for (int j = 0; j < prevPath.size() - 1; j++) {
                MetroNodeWithWeight spurNode = prevPath.get(j);
                MetroPath rootPath = prevPath.subPath(0, j + 1); // 루트 경로 계산

                // 루트 경로와 중복되지 않도록 기존 경로에서 간선을 제거
                List<MetroEdge> removedEdgeList = new ArrayList<>();
                for (MetroPath path : shortestPathList) {
                    if (path.size() > j && path.subPath(0, j + 1).equals(rootPath)) {
                        MetroEdge removedEdge = graphForPathSearch.removeEdge(path.get(j).getNode(), path.get(j + 1).getNode());
                        if (removedEdge != null) {
                            removedEdgeList.add(removedEdge);
                        }
                    }
                }

                MetroPath spurPath = searchPath(spurNode.getNodeNo(), destNo, rootPath);
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

        return shortestPathList;
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

    public void printDist(double[] dist) {
        for (int i = 0; i < dist.length; i++) {
//            MetroNode node = metroMap.getNode(i);
            MetroNode node = graphForPathSearch.get(i);
            System.out.printf("%s\t%s\t%.1f\n", node.getLnCd(), node.getStinNm(), dist[i]);
        }
    }
}

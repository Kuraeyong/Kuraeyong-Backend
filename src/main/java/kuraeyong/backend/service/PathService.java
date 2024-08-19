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

        return searchPath(orgNo, destNo);
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
    private MetroPath searchPath(int orgNo, int destNo) {
        int graphSize = graphForPathSearch.size();
        boolean[] check = new boolean[graphSize];
        double[] dist = new double[graphSize];
        MetroNodeWithWeight[] prevNode = new MetroNodeWithWeight[graphSize];

        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[orgNo] = 0;

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
        return createPath(prevNode, orgNo, destNo);
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

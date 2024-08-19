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

    private final int TRF_STIN_CNT = 269;
    private final MetroMap metroMap;
    private final List<MetroNode> graphForPathSearch;

    public MetroPath searchPath(String orgRailOprIsttCd, String orgLnCd, String orgStinCd,
                           String destRailOprIsttCd, String destLnCd, String destStinCd) {
        initForPathSearch();
//        System.out.println(metroMap);
        int orgNo = addNode(orgRailOprIsttCd, orgLnCd, orgStinCd);
        int destNo = addNode(destRailOprIsttCd, destLnCd, destStinCd);
        updateEdgeList(orgNo);
        updateEdgeList(destNo);

        return searchPath(orgNo, destNo);
    }

    /**
     * 일반역을 추가하는 함수
     * edgeInfoList 중곡->상봉, 중곡->군자
     * node         중곡
     * trfNode      상봉, (군자)
     * edge         중곡->상봉
     */
    private int addNode(String railOprIsttCd, String lnCd, String stinCd) {
        List<EdgeInfo> edgeInfoList = metroMap.getEdgeInfoRepository().findByRailOprIsttCdAndLnCdAndStinCd(railOprIsttCd, lnCd, stinCd);
        if (edgeInfoList.get(0).getIsTrfStin() == 0) {
            // 새로운 노드(일반역) 생성
            MetroNode node = MetroNode.builder()
                    .edgeList(new ArrayList<>())
                    .railOprIsttCd(railOprIsttCd)
                    .lnCd(lnCd)
                    .stinCd(stinCd)
                    .stinNm(edgeInfoList.get(0).getStinNm())
                    .nodeNo(graphForPathSearch.size())
                    .build();

            // 새로운 노드에 간선 추가
            for (EdgeInfo edgeInfo : edgeInfoList) {
                MetroNode trfNode = metroMap.getNode(edgeInfo.getTrfRailOprIsttCd(), edgeInfo.getTrfLnCd(), edgeInfo.getTrfStinCd());
                MetroEdge edge = MetroEdge.builder()
                        .trfRailOprIsttCd(edgeInfo.getTrfRailOprIsttCd())
                        .trflnCd(edgeInfo.getTrfLnCd())
                        .trfStinCd(edgeInfo.getTrfStinCd())
                        .trfStinNm(edgeInfo.getTrfStinNm())
                        .weight(edgeInfo.getWeight())
                        .trfNodeNo(trfNode.getNodeNo())
                        .build();
                node.addEdge(edge);
//                node.getEdgeList().add(edge);
            }

            graphForPathSearch.add(node);
            return node.getNodeNo();
        }
        return metroMap.getNode(railOprIsttCd, lnCd, stinCd).getNodeNo();
    }

    /**
     * 일반역을 추가함에 따라, 기존 환승역의 간선 리스트를 갱신하는 함수
     * node                 중곡
     * connectedTrfStinList 군자, 상봉
     * node.getEdgeList()   중곡->군자, (중곡->상봉)
     * trfNode              군자
     * newEdge              군자->중곡
     */
    private void updateEdgeList(int nodeNo) {
        // 기존에 있는 역(환승역)이었다면 간선 리스트를 갱신하지 않음
        if (nodeNo < TRF_STIN_CNT) {
            return;
        }
        MetroNode node = graphForPathSearch.get(nodeNo);
        List<MetroNode> connectedTrfStinList = new ArrayList<>();
        for (MetroEdge edge : node.getEdgeList()) {
            MetroNode trfNode = graphForPathSearch.get(edge.getTrfNodeNo());
            connectedTrfStinList.add(trfNode);
            MetroEdge newEdge = MetroEdge.builder()
                    .trfRailOprIsttCd(node.getRailOprIsttCd())
                    .trflnCd(node.getLnCd())
                    .trfStinCd(node.getStinCd())
                    .trfStinNm(node.getStinNm())
                    .weight(edge.getWeight())
                    .trfNodeNo(node.getNodeNo())
                    .build();
            trfNode.addEdge(newEdge);
        }

        // 일반역이 하나의 환승역에만 연결된 경우
        if (connectedTrfStinList.size() == 1) {
            return;
        }
        removeEdge(connectedTrfStinList.get(0), connectedTrfStinList.get(1));
        removeEdge(connectedTrfStinList.get(1), connectedTrfStinList.get(0));
    }

    private void removeEdge(MetroNode src, MetroNode dest) {
//        for (MetroEdge edge : src.getEdgeList()) {
//            if (edge.getTrfNodeNo() == dest.getNodeNo()) {
//                src.getEdgeList().remove(edge);
//            }
//        }
        src.getEdgeList().removeIf(edge -> edge.getTrfNodeNo() == dest.getNodeNo());
    }

    private void initForPathSearch() {
        graphForPathSearch.clear();
        for (MetroNode node : metroMap.getGraph()) {
            graphForPathSearch.add(new MetroNode(node));    // 깊은 복사
        }
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

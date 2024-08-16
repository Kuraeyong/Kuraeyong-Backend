package kuraeyong.backend.service;

import kuraeyong.backend.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

@Slf4j
@Service
@RequiredArgsConstructor
public class PathService {

    private final int TRF_STIN_CNT = 269;
    private final MetroMap metroMap;
    private final List<MetroNode> graphForPathSearch;

    public void searchPath(String orgRailOprIsttCd, String orgLnCd, String orgStinCd,
                           String destRailOprIsttCd, String destLnCd, String destStinCd) {
        initForPathSearch();
//        System.out.println(metroMap);
        int orgNo = addNode(orgRailOprIsttCd, orgLnCd, orgStinCd);
        int destNo = addNode(destRailOprIsttCd, destLnCd, destStinCd);
        updateEdgeList(orgNo);
        updateEdgeList(destNo);
        searchPath(orgNo);
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

    private void searchPath(int orgNo) {
        int graphSize = graphForPathSearch.size();
        boolean[] check = new boolean[graphSize];
        double[] dist = new double[graphSize];

        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[orgNo] = 0;

        PriorityQueue<NodeForPathSearch> pq = new PriorityQueue<>();
//        pq.offer(new NodeForPathSearch(metroMap.getNode(orgNo), 0));
        pq.offer(new NodeForPathSearch(graphForPathSearch.get(orgNo), 0));

        while (!pq.isEmpty()) {
            NodeForPathSearch now = pq.poll();
            int nowNo = now.getNodeNo();

            if (check[nowNo]) {
                continue;
            }
            check[nowNo] = true;

            for (MetroEdge edge : now.getEdgeList()) {
                double weight = edge.getWeight();
                if (dist[edge.getTrfNodeNo()] > dist[nowNo] + weight) {
                    dist[edge.getTrfNodeNo()] = dist[nowNo] + weight;
//                    pq.offer(new NodeForPathSearch(metroMap.getNode(edge.getTrfNodeNo()), dist[edge.getTrfNodeNo()]));
                    pq.offer(new NodeForPathSearch(graphForPathSearch.get(edge.getTrfNodeNo()), dist[edge.getTrfNodeNo()]));
                }
            }
        }

        printPath(dist);
    }

    private void printPath(double[] dist) {
        for (int i = 0; i < dist.length; i++) {
//            MetroNode node = metroMap.getNode(i);
            MetroNode node = graphForPathSearch.get(i);
            System.out.printf("%s\t%s\t%.1f\n", node.getLnCd(), node.getStinNm(), dist[i]);
        }
    }
}

package kuraeyong.backend.manager;

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
import java.util.PriorityQueue;
import java.util.Stack;

@Component
@RequiredArgsConstructor
public class TemporaryPathManager {
    private final StationTimeTableMap stationTimeTableMap;
    private final GraphForPathSearch graphForPathSearch;

    /**
     * 다익스트라 알고리즘을 통해, 하나의 임시 경로를 생성
     *
     * @param orgNo      출발역 번호
     * @param destNo     도착역 번호
     * @param rootPath   현재 생성하려는 임시 경로가 스퍼 경로인 경우에 필요한 루트 경로 정보
     * @param containExp 급행 간선 포함 여부
     * @param dateType   요일 종류
     * @return 임시 경로
     */
    public MetroPath create(int orgNo, int destNo, MetroPath rootPath, boolean containExp, String dateType) {
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
     * 다익스트라 알고리즘에서 나온 PrevNode를 역추적함에 따라, 임시 경로를 생성
     *
     * @param prevNode 이전 노드들
     * @param orgNo    출발역 번호
     * @param destNo   도착역 번호
     * @return 임시 경로
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
}

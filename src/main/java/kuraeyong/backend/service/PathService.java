package kuraeyong.backend.service;

import kuraeyong.backend.domain.MetroEdge;
import kuraeyong.backend.domain.MetroMap;
import kuraeyong.backend.domain.MetroNode;
import kuraeyong.backend.domain.NodeForPathSearch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.PriorityQueue;

@Slf4j
@Service
@RequiredArgsConstructor
public class PathService {

    private final MetroMap metroMap;
    private final int TRF_STIN_CNT = 269;
    private final int INF = Integer.MAX_VALUE;

    public void pathSearch(int start) {
        boolean[] check = new boolean[TRF_STIN_CNT];
        int[] dist = new int[TRF_STIN_CNT];

        Arrays.fill(dist, INF);
        dist[start] = 0;

        metroMap.initMap();
        PriorityQueue<NodeForPathSearch> pq = new PriorityQueue<>();
        pq.offer(new NodeForPathSearch(metroMap.getNode(start), 0));

        while (!pq.isEmpty()) {
            NodeForPathSearch now = pq.poll();
            int nowNo = now.getNodeNo();

            if (check[nowNo]) {
                continue;
            }
            check[nowNo] = true;

            for (MetroEdge edge : now.getEdgeList()) {
                int weight = edge.getWeight();
                if (dist[edge.getTrfNodeNo()] > dist[nowNo] + weight) {
                    dist[edge.getTrfNodeNo()] = dist[nowNo] + weight;
                    pq.offer(new NodeForPathSearch(metroMap.getNode(edge.getTrfNodeNo()), dist[edge.getTrfNodeNo()]));
                }
            }
        }

        printPath(dist);
    }

    private void printPath(int[] dist) {
        for (int i = 0; i < dist.length; i++) {
            MetroNode node = metroMap.getNode(i);
            System.out.printf("%s\t%s\t%d\n", node.getLnCd(), node.getStinNm(), dist[i]);
        }
    }
}

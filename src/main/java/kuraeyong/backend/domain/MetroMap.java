package kuraeyong.backend.domain;

import kuraeyong.backend.repository.EdgeInfoRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@RequiredArgsConstructor
public class MetroMap {
    private List<MetroNode> graph;

    private final EdgeInfoRepository edgeInfoRepository;

    public void initMap() {
        graph = new ArrayList<>();

        MetroNode node = new MetroNode();
        EdgeInfo prevEdgeInfo = new EdgeInfo();
        for (EdgeInfo edgeInfo : edgeInfoRepository.findByIsTrfStinGreaterThan(0)) {
            if (!isSameNode(edgeInfo, prevEdgeInfo)) {
                if (node.getEdgeList() != null) {  // 최초에 노드가 깡통인 경우를 방지
                    graph.add(node);
                }
                node = MetroNode.builder()
                        .railOprIsttCd(edgeInfo.getRailOprIsttCd())
                        .lnCd(edgeInfo.getLnCd())
                        .stinCd(edgeInfo.getStinCd())
                        .stinNm(edgeInfo.getStinNm())
                        .edgeList(new ArrayList<>())
                        .build();
            }

            MetroEdge edge = MetroEdge.builder()
                    .trfRailOprIsttCd(edgeInfo.getTrfRailOprIsttCd())
                    .trflnCd(edgeInfo.getTrfLnCd())
                    .trfStinCd(edgeInfo.getTrfStinCd())
                    .trfStinNm(edgeInfo.getTrfStinNm())
                    .weight(edgeInfo.getWeight())
                    .build();
            node.addEdge(edge);
            prevEdgeInfo = edgeInfo;
        }
        graph.add(node);    // 마지막 노드 개별 추가
    }

    public void printMap() {
        int count = 1;
        for (MetroNode node : graph) {
            System.out.printf("%d: %s\n", count++, node);
        }
    }

    private boolean isSameNode(EdgeInfo edgeInfo, EdgeInfo prevEdgeInfo) {
        return edgeInfo.getRailOprIsttCd().equals(prevEdgeInfo.getRailOprIsttCd()) &&
                edgeInfo.getLnCd().equals(prevEdgeInfo.getLnCd()) &&
                edgeInfo.getStinCd().equals(prevEdgeInfo.getStinCd());
    }
}


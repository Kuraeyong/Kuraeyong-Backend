package kuraeyong.backend.domain;

import kuraeyong.backend.repository.EdgeInfoRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Getter
@RequiredArgsConstructor
public class MetroMap {
    private List<MetroNode> graph;
    private HashMap<String, Integer> lineSeparator;
    private final EdgeInfoRepository edgeInfoRepository;

    public void initMap() {
        graph = new ArrayList<>();
        lineSeparator = new HashMap<>();

        MetroNode node = new MetroNode();
        EdgeInfo prevEdgeInfo = new EdgeInfo();
        String prevLnCd = "";
        int nodeNo = 0;
        for (EdgeInfo edgeInfo : edgeInfoRepository.findByIsTrfStinGreaterThan(0)) {
            if (!isSameLine(edgeInfo.getLnCd(), prevLnCd)) {
                lineSeparator.put(edgeInfo.getLnCd(), nodeNo);
            }
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
                        .nodeNo(nodeNo++)
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
            prevLnCd = edgeInfo.getLnCd();
        }
        graph.add(node);    // 마지막 노드 개별 추가

        initTrfNodeNo();
    }

    private void initTrfNodeNo() {
        for (MetroNode node : graph) {
            for (MetroEdge edge : node.getEdgeList()) {
                MetroNode trfNode = getNode(edge.getTrfRailOprIsttCd(), edge.getTrflnCd(), edge.getTrfStinCd());
                edge.setTrfNodeNo(trfNode.getNodeNo());
            }
        }
    }

    public void printMap() {
        for (MetroNode node : graph) {
            System.out.println(node);
        }
    }

    public MetroNode getNode(int idx) {
        return graph.get(idx);
    }

    public MetroNode getNode(String railOprIsttCd, String lnCd, String stinCd) {
        for (int idx = getLineStartIdx(lnCd); idx < graph.size(); idx++) {
            MetroNode node = getNode(idx);
            if (railOprIsttCd.equals(node.getRailOprIsttCd()) && stinCd.equals(node.getStinCd())) {
                return node;
            }
        }
        return null;
    }

    private int getLineStartIdx(String lnCd) {  // 해당 라인 코드가 없으면 null
        return lineSeparator.get(lnCd);
    }

    private boolean isSameNode(EdgeInfo edgeInfo, EdgeInfo prevEdgeInfo) {
        return edgeInfo.getRailOprIsttCd().equals(prevEdgeInfo.getRailOprIsttCd()) &&
                edgeInfo.getLnCd().equals(prevEdgeInfo.getLnCd()) &&
                edgeInfo.getStinCd().equals(prevEdgeInfo.getStinCd());
    }

    private boolean isSameLine(String lnCd, String prevLnCd) {
        return lnCd.equals(prevLnCd);
    }
}


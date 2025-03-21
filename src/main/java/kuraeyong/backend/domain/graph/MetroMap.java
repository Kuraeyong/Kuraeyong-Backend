package kuraeyong.backend.domain.graph;

import jakarta.annotation.PostConstruct;
import kuraeyong.backend.domain.constant.EdgeType;
import kuraeyong.backend.domain.station.info.MinimumStationInfo;
import kuraeyong.backend.domain.station.info.StationInfoMap;
import kuraeyong.backend.manager.station.EdgeInfoManager;
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
    private final EdgeInfoManager edgeInfoManager;
    private final StationInfoMap stationInfoMap;
    private List<MetroNode> graph;
    private HashMap<String, Integer> lineSeparator;

    @PostConstruct
    public void initMap() {
        graph = new ArrayList<>();
        lineSeparator = new HashMap<>();

        MetroNode node = new MetroNode();
        EdgeInfo prevEdgeInfo = new EdgeInfo();
        String prevLnCd = "";
        int nodeNo = 0;

        // 일반 간선 정보 조회
        for (EdgeInfo edgeInfo : edgeInfoManager.findNotExpEdgeInfo()) {
            if (!isSameLine(edgeInfo.getLnCd(), prevLnCd)) {
                lineSeparator.put(edgeInfo.getLnCd(), nodeNo);
            }
            if (!isSameNode(edgeInfo, prevEdgeInfo)) {
                if (node.getEdgeList() != null) {  // 최초에 노드가 깡통인 경우를 방지
                    graph.add(node);
                }
                String railOprIsttCd = edgeInfo.getRailOprIsttCd();
                String lnCd = edgeInfo.getLnCd();
                String stinCd = edgeInfo.getStinCd();
                MinimumStationInfo key = MinimumStationInfo.build(railOprIsttCd, lnCd, stinCd);
                node = MetroNode.builder()
                        .railOprIsttCd(railOprIsttCd)
                        .lnCd(lnCd)
                        .stinCd(stinCd)
                        .stinNm(edgeInfo.getStinNm())
                        .edgeList(new ArrayList<>())
                        .nodeNo(nodeNo++)
                        .isJctStin(edgeInfo.getIsJctStin())
                        .isExpStin(edgeInfo.getIsExpStin())
                        .upDownOrder(stationInfoMap.getUpDownOrder(key))
                        .branchInfo(stationInfoMap.getBranchInfo(key))
                        .build();
            }

            MetroEdge edge = MetroEdge.builder()
                    .trfRailOprIsttCd(edgeInfo.getTrfRailOprIsttCd())
                    .trfLnCd(edgeInfo.getTrfLnCd())
                    .trfStinCd(edgeInfo.getTrfStinCd())
                    .trfStinNm(edgeInfo.getTrfStinNm())
                    .weight(edgeInfo.getWeight())
                    .edgeType(EdgeType.convert(edgeInfo.getEdgeType()))
                    .build();
            node.addEdge(edge);

            prevEdgeInfo = edgeInfo;
            prevLnCd = edgeInfo.getLnCd();
        }
        graph.add(node);    // 마지막 노드 개별 추가

        // 급행 간선 정보 조회
        for (EdgeInfo edgeInfo : edgeInfoManager.findByEdgeTypeEquals(1)) {
            MinimumStationInfo MSI = MinimumStationInfo.build(edgeInfo.getRailOprIsttCd(), edgeInfo.getLnCd(), edgeInfo.getStinCd());
            node = getNode(MSI);
            MetroEdge edge = MetroEdge.builder()
                    .trfRailOprIsttCd(edgeInfo.getTrfRailOprIsttCd())
                    .trfLnCd(edgeInfo.getTrfLnCd())
                    .trfStinCd(edgeInfo.getTrfStinCd())
                    .trfStinNm(edgeInfo.getTrfStinNm())
                    .weight(edgeInfo.getWeight())
                    .edgeType(EdgeType.convert(edgeInfo.getEdgeType()))
                    .build();
            node.addEdge(edge);
        }

        initTrfNodeNo();
    }

    private void initTrfNodeNo() {
        for (MetroNode node : graph) {
            for (MetroEdge edge : node.getEdgeList()) {
                MinimumStationInfo trfNodeMSI = MinimumStationInfo.build(edge.getTrfRailOprIsttCd(), edge.getTrfLnCd(), edge.getTrfStinCd());
                MetroNode trfNode = getNode(trfNodeMSI);
                edge.setTrfNodeNo(trfNode.getNodeNo());
            }
        }
    }

    public MetroNode getNode(int nodeNo) {
        return graph.get(nodeNo);
    }

    public MetroNode getNode(MinimumStationInfo MSI) {
        String railOprIsttCd = MSI.getRailOprIsttCd();
        String lnCd = MSI.getLnCd();
        String stinCd = MSI.getStinCd();

        for (int idx = getLineStartIdx(lnCd); idx < graph.size(); idx++) {
            MetroNode node = getNode(idx);
            if (railOprIsttCd.equals(node.getRailOprIsttCd()) && stinCd.equals(node.getStinCd())) {
                return node;
            }
        }
        return null;
    }

    public int getTrfOrExpStinCnt() {
        return graph.size();
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (MetroNode node : graph) {
            sb.append(node).append('\n');
        }
        return sb.toString();
    }
}


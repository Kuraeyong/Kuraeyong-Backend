package kuraeyong.backend.domain;

import jakarta.annotation.PostConstruct;
import kuraeyong.backend.dto.MinimumStationInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@RequiredArgsConstructor
public class GraphForPathSearch {

    //    private final int TRF_STIN_CNT = 269;
    private final int TRF_OR_EXP_STIN_CNT = 378;
    private List<MetroNode> graphForPathSearch;
    private final MetroMap metroMap;

    /**
     * 경로 탐색을 위한 그래프 초기화
     */
    @PostConstruct
    public void init() {
        graphForPathSearch = new ArrayList<>();
        for (MetroNode node : metroMap.getGraph()) {
            graphForPathSearch.add(new MetroNode(node));    // 깊은 복사
        }
    }

    /**
     * 일반역을 추가하는 함수
     *
     * @inline-variable edgeInfoList 중곡->상봉, 중곡->군자
     * @inline-variable node         중곡
     * @inline-variable trfNode      상봉, (군자)
     * @inline-variable edge         중곡->상봉
     */
    public int addNode(MinimumStationInfo minimumStationInfo) {
        String railOprIsttCd = minimumStationInfo.getRailOprIsttCd();
        String lnCd = minimumStationInfo.getLnCd();
        String stinCd = minimumStationInfo.getStinCd();
        MinimumStationInfo key = MinimumStationInfo.build(railOprIsttCd, lnCd, stinCd);
        List<EdgeInfo> edgeInfoList = metroMap.getEdgeInfoRepository().findByRailOprIsttCdAndLnCdAndStinCd(railOprIsttCd, lnCd, stinCd);
        EdgeInfo anyEdgeInfo = edgeInfoList.get(0);
        if (isGeneralStin(anyEdgeInfo)) {
            // 새로운 노드(일반역) 생성
            MetroNode node = MetroNode.builder()
                    .edgeList(new ArrayList<>())
                    .railOprIsttCd(railOprIsttCd)
                    .lnCd(lnCd)
                    .stinCd(stinCd)
                    .stinNm(anyEdgeInfo.getStinNm())
                    .nodeNo(graphForPathSearch.size())
                    .isJctStin(anyEdgeInfo.getIsJctStin())
                    .isExpStin(anyEdgeInfo.getIsExpStin())
                    .upDownOrder(metroMap.getStationInfoMap().getUpDownOrder(key))
                    .branchInfo(metroMap.getStationInfoMap().getBranchInfo(key))
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
                        .edgeType(EdgeType.intToEdgeType(edgeInfo.getEdgeType()))
                        .build();
                node.addEdge(edge);
            }

            graphForPathSearch.add(node);
            return node.getNodeNo();
        }
        return metroMap.getNode(railOprIsttCd, lnCd, stinCd).getNodeNo();
    }

    /**
     * @param edgeInfo 해당역의 임의의 간선
     * @return 일반역 여부 판별 (환승역도 아니고, 급행 정차역도 아닌지)
     */
    private static boolean isGeneralStin(EdgeInfo edgeInfo) {
        return edgeInfo.getIsTrfStin() == 0 && edgeInfo.getIsExpStin() == 0;
    }

    /**
     * 일반역을 추가함에 따라, 기존 환승역(또는 급행 정차역)의 간선 리스트를 갱신하는 함수
     *
     * @inline-variable node                 중곡
     * @inline-variable connectedTrfStinList 군자, 상봉
     * @inline-variable node.getEdgeList()   중곡->군자, (중곡->상봉)
     * @inline-variable trfNode              군자
     * @inline-variable newEdge              군자->중곡
     */
    public void updateEdgeList(int nodeNo) {
        // 기존에 있는 역(환승역이거나 급행 정차역)이었다면 간선 리스트를 갱신하지 않음
        if (nodeNo < TRF_OR_EXP_STIN_CNT) {
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
                    .edgeType(edge.getEdgeType())
                    .build();
            trfNode.addEdge(newEdge);
        }

        // 일반역이 하나의 환승역에만 연결된 경우
        if (connectedTrfStinList.size() == 1) {
            return;
        }
        removeEdge(connectedTrfStinList.get(0), connectedTrfStinList.get(1), EdgeType.GEN_EDGE);
        removeEdge(connectedTrfStinList.get(1), connectedTrfStinList.get(0), EdgeType.GEN_EDGE);
    }

    public void addEdge(MetroNode src, MetroEdge edge) {
        MetroNode node = get(src.getNodeNo());
        node.addEdge(edge);
    }

    /**
     * 인자로 GraphForPathSearch의 MetroNode를 주어야 제대로 동작함
     * @param edgeType 자를 간선의 종류
     * @return  자른 간선
     */
    public MetroEdge removeEdge(MetroNode src, MetroNode dest, EdgeType edgeType) {
        for (MetroEdge edge : src.getEdgeList()) {
            if (edge.getEdgeType() == edgeType &&
                    edge.getTrfNodeNo() == dest.getNodeNo()) {
                src.getEdgeList().remove(edge);
                return edge;
            }
        }
        return null;
    }

    public int size() {
        return graphForPathSearch.size();
    }

    public MetroNode get(int nodeNo) {
        return graphForPathSearch.get(nodeNo);
    }
}

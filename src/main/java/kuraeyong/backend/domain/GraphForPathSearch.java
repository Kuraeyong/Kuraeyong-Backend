package kuraeyong.backend.domain;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@RequiredArgsConstructor
public class GraphForPathSearch {

    private final int TRF_STIN_CNT = 269;
    private List<MetroNode> graphForPathSearch;
    private final MetroMap metroMap;

    @PostConstruct
    public void init() {
        graphForPathSearch = new ArrayList<>();
        for (MetroNode node : metroMap.getGraph()) {
            graphForPathSearch.add(new MetroNode(node));    // 깊은 복사
        }
    }

    /**
     * 일반역을 추가하는 함수
     * edgeInfoList 중곡->상봉, 중곡->군자
     * node         중곡
     * trfNode      상봉, (군자)
     * edge         중곡->상봉
     */
    public int addNode(String railOprIsttCd, String lnCd, String stinCd) {
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
    public void updateEdgeList(int nodeNo) {
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

    public void addEdge(MetroNode src, MetroEdge edge) {
        MetroNode node = get(src.getNodeNo());
        node.addEdge(edge);
    }

    /**
     * 인자로 GraphForPathSearch의 MetroNode를 주어야 제대로 동작함
     */
    public MetroEdge removeEdge(MetroNode src, MetroNode dest) {
        for (MetroEdge edge : src.getEdgeList()) {
            System.out.printf("edge(전): %s\n", edge);
        }
        for (MetroEdge edge : src.getEdgeList()) {
            if (edge.getTrfNodeNo() == dest.getNodeNo()) {
                src.getEdgeList().remove(edge);
                System.out.printf("%s, %s을(를) 잘랐어\n", edge.getTrflnCd(), edge.getTrfStinNm());
                for (MetroEdge edge1 : src.getEdgeList()) {
                    System.out.printf("edge(후): %s\n", edge1);
                }
                System.out.println();
                return edge;
            }
        }
        System.out.println("안잘랐어");
        System.out.println();
        System.out.println(src.getEdgeList());
        return null;
    }

    public int size() {
        return graphForPathSearch.size();
    }

    public MetroNode get(int nodeNo) {
        return graphForPathSearch.get(nodeNo);
    }
}

package kuraeyong.backend.manager;

import kuraeyong.backend.domain.constant.EdgeType;
import kuraeyong.backend.domain.graph.GraphForPathSearch;
import kuraeyong.backend.domain.graph.MetroNode;
import kuraeyong.backend.domain.path.MetroNodeWithEdge;
import kuraeyong.backend.domain.path.MetroPath;
import kuraeyong.backend.domain.station.info.MinimumStationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TemporaryPathsTopManager {
    private final GraphForPathSearch graphForPathSearch;
    private final TemporaryPathsManager temporaryPathsManager;

    /**
     * 임시 경로 목록 생성
     * 임시 경로 : 출발역, 도착역, 요일 종류만을 고려한 경로 (i.e. 실제 시간표와 대조하지 않은 경로)
     *
     * @param org      출발역의 MSI
     * @param dest     도착역의 MSI
     * @param dateType 요일 종류
     * @return 임시 경로 목록
     */
    public List<MetroPath> create(MinimumStationInfo org, MinimumStationInfo dest, String dateType) {
        // 경로 탐색용 그래프 초기화
        graphForPathSearch.init();
        int orgNo = graphForPathSearch.addNode(org);
        int destNo = graphForPathSearch.addNode(dest);
        graphForPathSearch.updateEdgeList(orgNo);
        graphForPathSearch.updateEdgeList(destNo);

        // 임시 경로 목록 생성
        List<MetroPath> temporaryPaths = temporaryPathsManager.create(orgNo, destNo, false, dateType);
        List<MetroPath> temporaryPathsWithExpEdge = temporaryPathsManager.create(orgNo, destNo, true, dateType);
        temporaryPaths.addAll(temporaryPathsWithExpEdge);
        temporaryPaths = getUniqueTemporaryPaths(temporaryPaths);
        addDirectPath(temporaryPaths, orgNo, destNo);
        return temporaryPaths;
    }

    /**
     * 고유한 임시 경로 목록을 반환
     *
     * @param temporaryPaths 임시 경로 목록
     * @return 고유한 임시 경로 목록
     */
    private List<MetroPath> getUniqueTemporaryPaths(List<MetroPath> temporaryPaths) {
        return new ArrayList<>(new LinkedHashSet<>(temporaryPaths));
    }

    /**
     * 두 일반역을 직선으로 잇는 경로를 추가
     *
     * @param temporaryPaths 임시 경로 목록
     * @param orgNo          출발역명
     * @param destNo         도착역명
     */
    private void addDirectPath(List<MetroPath> temporaryPaths, int orgNo, int destNo) {
        // 두 일반역을 직선으로 잇는 경로를 추가할 필요가 없는지 검사
        MetroNode org = graphForPathSearch.get(orgNo);
        MetroNode dest = graphForPathSearch.get(destNo);
        if (!org.getLnCd().equals(dest.getLnCd())) {    // 노선 환승이 필요한 경우
            return;
        }
        if (orgNo < graphForPathSearch.getTrfOrExpStinCnt() || destNo < graphForPathSearch.getTrfOrExpStinCnt()) {    // 하나라도 일반역이 아닌 경우
            return;
        }

        // 두 일반역을 직선으로 잇는 경로 하나 추가
        MetroPath directPath = new MetroPath(new ArrayList<>());
        directPath.addNode(MetroNodeWithEdge.builder()
                .node(new MetroNode(org))
                .edgeType(EdgeType.GEN_EDGE)
                .build());
        directPath.addNode(MetroNodeWithEdge.builder()
                .node(new MetroNode(dest))
                .edgeType(EdgeType.GEN_EDGE)
                .build());
        temporaryPaths.add(directPath);
    }
}

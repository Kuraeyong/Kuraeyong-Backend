package kuraeyong.backend.manager;

import kuraeyong.backend.common.exception.ErrorMessage;
import kuraeyong.backend.common.exception.PathSearchResultException;
import kuraeyong.backend.domain.constant.EdgeType;
import kuraeyong.backend.domain.graph.GraphForPathSearch;
import kuraeyong.backend.domain.graph.MetroEdge;
import kuraeyong.backend.domain.graph.MetroNode;
import kuraeyong.backend.domain.path.MetroNodeWithEdge;
import kuraeyong.backend.domain.path.MetroPath;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class TemporaryPathsManager {
    private final TemporaryPathManager temporaryPathManager;
    private final GraphForPathSearch graphForPathSearch;

    private static final int YEN_CANDIDATE_CNT = 10;

    /**
     * 옌 알고리즘을 통해 임시 경로 목록을 생성
     *
     * @param orgNo      출발역 번호
     * @param destNo     도착역 번호
     * @param containExp 급행 간선 포함 여부
     * @param dateType   요일 종류
     * @return 임시 경로 목록
     */
    public List<MetroPath> create(int orgNo, int destNo, boolean containExp, String dateType) {
        List<MetroPath> temporaryPathCandidates = new ArrayList<>();
        Set<MetroPath> uniqueTemporaryPathCandidates = new HashSet<>();
        PriorityQueue<MetroPath> sortedTemporaryPathCandidates = new PriorityQueue<>();

        // 첫 번째 최단 경로 계산
        MetroPath initialPath = temporaryPathManager.create(orgNo, destNo, null, containExp, dateType);
        if (initialPath == null) {
            throw new PathSearchResultException(ErrorMessage.TEMPORARY_PATH_NOT_FOUND);
        }
        temporaryPathCandidates.add(initialPath);
        uniqueTemporaryPathCandidates.add(initialPath);

        // 남은 (YEN_CANDIDATE_CNT-1)개의 경로를 탐색
        for (int i = 1; i < YEN_CANDIDATE_CNT; i++) {
            MetroPath prevPath = temporaryPathCandidates.get(i - 1);   // 이전에 찾은 최단 경로

            // 각 스퍼 노드에 대해 새로운 경로를 탐색
            for (int j = 0; j < prevPath.size() - 1; j++) {
                MetroNodeWithEdge spurNode = prevPath.get(j);
                MetroPath rootPath = prevPath.subPath(0, j + 1); // 루트 경로 계산

                // 루트 경로와 중복되지 않도록 기존 경로에서 간선을 제거
                List<MetroEdge> removedEdgeList = new ArrayList<>();
                for (MetroPath temporaryPathCandidate : temporaryPathCandidates) {
                    if (temporaryPathCandidate.size() > j + 1 && temporaryPathCandidate.subPath(0, j + 1).equals(rootPath)) {
                        MetroNode orgNode = graphForPathSearch.get(temporaryPathCandidate.get(j).getNodeNo());
                        MetroNode destNode = graphForPathSearch.get(temporaryPathCandidate.get(j + 1).getNodeNo());
                        MetroEdge removedGeneralEdge = graphForPathSearch.removeEdge(orgNode, destNode, EdgeType.GEN_EDGE);
                        MetroEdge removedExpressEdge = graphForPathSearch.removeEdge(orgNode, destNode, EdgeType.EXP_EDGE);
                        MetroEdge removedTransferEdge = graphForPathSearch.removeEdge(orgNode, destNode, EdgeType.TRF_EDGE);

                        if (removedGeneralEdge != null) {
                            removedEdgeList.add(removedGeneralEdge);
                        }
                        if (removedExpressEdge != null) {
                            removedEdgeList.add(removedExpressEdge);
                        }
                        if (removedTransferEdge != null) {
                            removedEdgeList.add(removedTransferEdge);
                        }
                    }
                }

                MetroPath spurPath = temporaryPathManager.create(spurNode.getNodeNo(), destNo, rootPath, containExp, dateType);
                if (spurPath != null) {
                    MetroPath totalPath = new MetroPath(rootPath);
                    totalPath.concat(spurPath, true);
                    if (!uniqueTemporaryPathCandidates.contains(totalPath)) {
                        sortedTemporaryPathCandidates.add(totalPath);
                        uniqueTemporaryPathCandidates.add(totalPath);
                    }
                }

                // 제거된 간선을 복원
                for (MetroEdge edge : removedEdgeList) {
                    graphForPathSearch.addEdge(spurNode.getNode(), edge);
                }
            }

            // 후보 경로 중 최단 경로를 선택하여, 최단 경로 리스트에 추가
            if (sortedTemporaryPathCandidates.isEmpty()) {
                break;
            }
            temporaryPathCandidates.add(sortedTemporaryPathCandidates.poll());
        }
        return selectUpperRankedCandidates(temporaryPathCandidates, sortedTemporaryPathCandidates);
    }

    /**
     * 임시 경로 후보 목록에서 상위 {YEN_CANDIDATE_CNT}개의 임시 경로 후보들을 선정
     *
     * @param temporaryPathCandidates       임시 경로 후보 목록
     * @param sortedTemporaryPathCandidates 우선순위에 따라 정렬된 임시 경로 후보 목록
     * @return 상위 랭크의 임시 경로 후보 목록
     */
    private List<MetroPath> selectUpperRankedCandidates(List<MetroPath> temporaryPathCandidates, PriorityQueue<MetroPath> sortedTemporaryPathCandidates) {
        // 불필요한 경로 제거 후, 중복을 제거하기 위해 pathSet에 모두 집합
        Set<MetroPath> uniqueTemporaryPathCandidates = new HashSet<>();
        for (MetroPath temporaryPathCandidate : temporaryPathCandidates) {
            temporaryPathCandidate.removeUnnecessaryPath();
            uniqueTemporaryPathCandidates.add(temporaryPathCandidate);
        }
        for (MetroPath sortedTemporaryPathCandidate : sortedTemporaryPathCandidates) {
            sortedTemporaryPathCandidate.removeUnnecessaryPath();
            uniqueTemporaryPathCandidates.add(sortedTemporaryPathCandidate);
        }

        // 정렬을 위해 우선순위 큐에 삽입
        sortedTemporaryPathCandidates.clear();
        sortedTemporaryPathCandidates.addAll(uniqueTemporaryPathCandidates);

        // 상위 k개에 대해서 조회
        List<MetroPath> temporaryPaths = new ArrayList<>();
        while (!sortedTemporaryPathCandidates.isEmpty() && temporaryPaths.size() < YEN_CANDIDATE_CNT) {
            MetroPath sortedTemporaryPathCandidate = sortedTemporaryPathCandidates.poll();
            if (isEfficientTemporaryPath(sortedTemporaryPathCandidate)) {
                temporaryPaths.add(sortedTemporaryPathCandidate);
            }
        }
        return temporaryPaths;
    }

    /**
     * 효율적인 임시 경로 여부를 반환
     *
     * @param temporaryPath 임시 경로
     * @return 효율적인 임시 경로 여부
     */
    private boolean isEfficientTemporaryPath(MetroPath temporaryPath) {
        HashMap<String, Integer> firstOccurrenceIdx = new HashMap<>();

        for (int idx = 0; idx < temporaryPath.size(); idx++) {
            String stinNm = temporaryPath.get(idx).getStinNm();
            if (!firstOccurrenceIdx.containsKey(stinNm)) {  // 처음 등장하는 역명은 맵에 추가
                firstOccurrenceIdx.put(stinNm, idx);
                continue;
            }
            if (firstOccurrenceIdx.get(stinNm) + 1 != idx) {    // 동일 역명이 연속된 경로가 아니라면
                return false;
            }
        }
        return true;
    }
}
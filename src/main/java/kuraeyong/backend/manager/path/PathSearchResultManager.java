package kuraeyong.backend.manager.path;

import kuraeyong.backend.domain.constant.DirectionType;
import kuraeyong.backend.domain.path.ActualPath;
import kuraeyong.backend.domain.path.MetroPath;
import kuraeyong.backend.domain.path.MoveInfo;
import kuraeyong.backend.domain.path.MoveInfos;
import kuraeyong.backend.domain.path.PathSearchResult;
import kuraeyong.backend.domain.path.PathSearchSegment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PathSearchResultManager {

    /**
     * 최적의 실제 경로를 이용해, 경로 탐색 결과를 생성
     *
     * @param optimalPath    최적의 실제 경로
     * @param stopoverStinNm 경유역 이름
     * @param stopoverTime   경유역에서 경유하는 시간
     * @return 경로 탐색 결과
     */
    public PathSearchResult create(ActualPath optimalPath, String stopoverStinNm, int stopoverTime) {
        // init
        MoveInfos moveInfos = optimalPath.getMoveInfos();
        MetroPath compressedPath = optimalPath.getCompressedPath();
        List<PathSearchSegment> pathSearchSegments = new ArrayList<>();

        // create
        int firstMoveInfoIdxWithSameTrn = 1;
        MoveInfo firstMoveInfoWithSameTrn;
        for (int i = 2; i < moveInfos.size(); i++) {
            MoveInfo prev = moveInfos.get(i - 1);
            MoveInfo curr = moveInfos.get(i);

            if (curr.getTrnGroupNo() == prev.getTrnGroupNo()) {
                continue;
            }
            firstMoveInfoWithSameTrn = moveInfos.get(firstMoveInfoIdxWithSameTrn);
            pathSearchSegments.add(PathSearchSegment.of(
                    determineLnCd(prev.getLnCd()),
                    compressedPath.getStinNm(firstMoveInfoIdxWithSameTrn - 1),
                    compressedPath.getStinNm(i - 1),
                    firstMoveInfoWithSameTrn.getDptTm(),
                    prev.getArvTm(),
                    firstMoveInfoWithSameTrn.getTmnStinNm(),
                    DirectionType.get(firstMoveInfoWithSameTrn.getTrnNo())
            ));
            firstMoveInfoIdxWithSameTrn = i;    // update

            // 일반, 급행 환승인 경우
            if (curr.getTrnGroupNo() == -1 || prev.getTrnGroupNo() == -1) {
                continue;
            }
            pathSearchSegments.add(PathSearchSegment.of(
                    determineLnCd(null),
                    compressedPath.getStinNm(i - 1),
                    compressedPath.getStinNm(i - 1),
                    prev.getArvTm(),
                    prev.getArvTm(),
                    null,
                    null
            ));
        }
        // 마지막 PathSearchSegment 별도 추가
        firstMoveInfoWithSameTrn = moveInfos.get(firstMoveInfoIdxWithSameTrn);
        pathSearchSegments.add(PathSearchSegment.of(
                determineLnCd(moveInfos.get(moveInfos.size() - 1).getLnCd()),
                compressedPath.getStinNm(firstMoveInfoIdxWithSameTrn - 1),
                compressedPath.getStinNm(compressedPath.size() - 1),
                firstMoveInfoWithSameTrn.getDptTm(),
                moveInfos.get(moveInfos.size() - 1).getArvTm(),
                firstMoveInfoWithSameTrn.getTmnStinNm(),
                DirectionType.get(firstMoveInfoWithSameTrn.getTrnNo())
        ));
        return new PathSearchResult(pathSearchSegments, optimalPath.getTotalTime(), optimalPath.getCongestionScore(), stopoverStinNm, stopoverTime);
    }

    private String determineLnCd(String lnCd) {
        if (lnCd == null) {
            return "환승";
        }
        return lnCd;
    }
}
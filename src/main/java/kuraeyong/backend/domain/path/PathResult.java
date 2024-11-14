package kuraeyong.backend.domain.path;

import kuraeyong.backend.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class PathResult implements Comparable<PathResult> {
    private final MetroPath path;
    private final MetroPath compressedPath;
    private final MoveInfos moveInfos;
    @Setter
    private int congestionScore;

    public PathResult(MetroPath path, MoveInfos moveInfos) {
        this.path = path;
        this.compressedPath = path.getCompressPath();
        this.moveInfos = moveInfos;
    }

    public List<MetroNodeWithEdge> getIterablePath() {
        return path.getPath();
    }

    public String getFinalArvTm() {
        return moveInfos.getArvTm(moveInfos.size() - 1);
    }

    public int getTotalTime() {
        return DateUtil.getMinDiff(moveInfos.getArvTm(0), moveInfos.getArvTm(moveInfos.size() - 1));
    }

    public int getTrfCnt() {
        return moveInfos.getTrfCnt();
    }

    public int getTotalTrfTime() {
        return moveInfos.getTotalTrfTime();
    }

    public UserMoveInfos createUserMoveInfos() {
        List<UserMoveInfo> userMoveInfos = new ArrayList<>();

        int firstMoveInfoIdxWithSameTrn = 1;
        for (int i = 2; i < moveInfos.size(); i++) {
            MoveInfo prev = moveInfos.get(i - 1);
            MoveInfo curr = moveInfos.get(i);

            if (curr.getTrnGroupNo() == prev.getTrnGroupNo()) {
                continue;
            }
            userMoveInfos.add(UserMoveInfo.of(
                    prev.getLnCd(),
                    compressedPath.getStinNm(firstMoveInfoIdxWithSameTrn - 1),
                    compressedPath.getStinNm(i - 1),
                    moveInfos.get(firstMoveInfoIdxWithSameTrn).getDptTm(),
                    prev.getArvTm()
            ));
            firstMoveInfoIdxWithSameTrn = i;

            // 일반, 급행 환승인 경우
            if (curr.getTrnGroupNo() == -1 || prev.getTrnGroupNo() == -1) {
                continue;
            }
            userMoveInfos.add(UserMoveInfo.of(
                    null,
                    compressedPath.getStinNm(i - 1),
                    compressedPath.getStinNm(i - 1),
                    prev.getArvTm(),
                    prev.getArvTm()
            ));
        }
        // 마지막 UserMoveInfo 별도 추가
        userMoveInfos.add(UserMoveInfo.of(
                moveInfos.get(moveInfos.size() - 1).getLnCd(),
                compressedPath.getStinNm(firstMoveInfoIdxWithSameTrn - 1),
                compressedPath.getStinNm(compressedPath.size() - 1),
                moveInfos.get(firstMoveInfoIdxWithSameTrn).getDptTm(),
                moveInfos.get(moveInfos.size() - 1).getArvTm()
        ));

        return new UserMoveInfos(userMoveInfos, getTotalTime(), congestionScore);
    }

    @Override
    public String toString() {
        return path + "\n" +
                compressedPath + "\n" +
                moveInfos +
                createUserMoveInfos();
    }

    @Override
    public int compareTo(PathResult o) {
        if (!getFinalArvTm().equals(o.getFinalArvTm())) {    // 도착시간이 다르면
            return getFinalArvTm().compareTo(o.getFinalArvTm());
        }
        if (getTrfCnt() != o.getTrfCnt()) {    // 환승횟수가 다르면
            return Integer.compare(getTrfCnt(), o.getTrfCnt());
        }
        return Integer.compare(getTotalTrfTime(), o.getTotalTrfTime());
    }
}

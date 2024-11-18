package kuraeyong.backend.domain.path;

import kuraeyong.backend.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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

    @Override
    public String toString() {
        return path + "\n" +
                compressedPath + "\n" +
                moveInfos + "\n" +
                congestionScore;
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

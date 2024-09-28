package kuraeyong.backend.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PathSearchElement implements Comparable<PathSearchElement> {
    private final MetroPath compressedPath;
    private final MoveInfoList moveInfoList;

    public String getFinalArvTm() {
        return moveInfoList.getFinalArvTm();
    }

    public int getTrfCnt() {
        return moveInfoList.getTrfCnt();
    }

    public int getTotalTrfTime() {
        return moveInfoList.getTotalTrfTime();
    }

    @Override
    public String toString() {
        return compressedPath + "\n" + moveInfoList;
    }

    @Override
    public int compareTo(PathSearchElement o) {
        if (!getFinalArvTm().equals(o.getFinalArvTm())){    // 도착시간이 다르면
            return getFinalArvTm().compareTo(o.getFinalArvTm());
        }
        if (getTrfCnt() != o.getTrfCnt()){    // 환승횟수가 다르면
            return Integer.compare(getTrfCnt(), o.getTrfCnt());
        }
        return Integer.compare(getTotalTrfTime(), o.getTotalTrfTime());
    }
}

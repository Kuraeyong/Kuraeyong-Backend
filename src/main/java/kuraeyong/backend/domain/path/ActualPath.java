package kuraeyong.backend.domain.path;

import kuraeyong.backend.domain.station.info.MinimumStationInfo;
import kuraeyong.backend.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ActualPath implements Comparable<ActualPath> {
    private final MetroPath path;
    private final MetroPath compressedPath;
    private final MoveInfos moveInfos;
    @Setter
    private int congestionScore;

    public ActualPath(MetroPath path, MetroPath compressedPath, MoveInfos moveInfos) {
        this.path = path;
        this.compressedPath = compressedPath;
        this.moveInfos = moveInfos;
        setPassingTimeOfPath();
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

    /**
     * 압축 경로 노드의 passingTime을 기반으로, 일반 경로 노드의 passingTime을 설정
     */
    private void setPassingTimeOfPath() {
        // 압축 경로 노드의 passingTime을 일반 경로의 동일한 역에도 반영
        for (MetroNodeWithEdge compressedPathNode : compressedPath.getPath()) {
            for (MetroNodeWithEdge node : path.getPath()) {
                if (!MinimumStationInfo.get(node).equals(MinimumStationInfo.get(compressedPathNode))) {
                    continue;
                }
                node.setPassingTime(compressedPathNode.getPassingTime());
            }
        }

        // 일반 경로 모든역의 passingTime 설정
        int size = path.size();
        int lastPassingTimeIdx = 0;
        for (int i = 1; i < size; i++) {
            MetroNodeWithEdge curr = path.get(i);
            if (curr.getPassingTime() == null) {
                continue;
            }
            // i가 압축 경로 경유역인 경우
            int increment = DateUtil.getMinDiff(path.get(lastPassingTimeIdx).getPassingTime(), curr.getPassingTime()) / (i - lastPassingTimeIdx);
            for (int j = lastPassingTimeIdx + 1, incrementCnt = 1; j < i; j++) {
                path.get(j).setPassingTime(DateUtil.plusMinutes(path.get(lastPassingTimeIdx).getPassingTime(), increment * incrementCnt++));
            }
            lastPassingTimeIdx = i;
        }

        // 압축 경로의 마지막 경유역 처리
        MetroNodeWithEdge lastPassingTimeNode = path.get(lastPassingTimeIdx);
        MetroNodeWithEdge lastNode = path.get(size - 1);
        int increment = DateUtil.getMinDiff(lastPassingTimeNode.getPassingTime(), lastNode.getPassingTime()) / (size - lastPassingTimeIdx);
        for (int j = lastPassingTimeIdx + 1, incrementCnt = 1; j < size; j++) {
            path.get(j).setPassingTime(DateUtil.plusMinutes(lastPassingTimeNode.getPassingTime(), increment * incrementCnt++));
        }
    }

    @Override
    public String toString() {
        return path + "\n" +
                compressedPath + "\n" +
                moveInfos + "\n" +
                congestionScore;
    }

    @Override
    public int compareTo(ActualPath o) {
        if (!getFinalArvTm().equals(o.getFinalArvTm())) {    // 도착시간이 다르면
            return Integer.compare(DateUtil.getTimeForCompare(getFinalArvTm()), DateUtil.getTimeForCompare(o.getFinalArvTm()));
        }
        if (getTrfCnt() != o.getTrfCnt()) {    // 환승횟수가 다르면
            return Integer.compare(getTrfCnt(), o.getTrfCnt());
        }
        return Integer.compare(getTotalTrfTime(), o.getTotalTrfTime());
    }
}

package kuraeyong.backend.domain.path;

import kuraeyong.backend.dto.PathSearchSegmentDto;
import kuraeyong.backend.util.DateUtil;
import kuraeyong.backend.util.StringUtil;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PathSearchResult {
    @Getter(AccessLevel.NONE)
    private final List<PathSearchSegment> pathSearchSegments;
    private final int totalRequiredTime;
    private final int congestionScore;

    public PathSearchResult(List<PathSearchSegment> pathSearchSegments, int totalRequiredTime, int congestionScore, String stopoverStinNm, int stopoverTime) {
        this.pathSearchSegments = pathSearchSegments;
        this.totalRequiredTime = totalRequiredTime;
        this.congestionScore = congestionScore;
        setStopoverInfo(stopoverStinNm, stopoverTime);
    }

    public int getTotalTrfCnt() {
        int totalTrfCount = 0;
        for (PathSearchSegment pathSearchSegment : pathSearchSegments) {
            if (pathSearchSegment.isTrf()) {
                totalTrfCount++;
            }
        }
        return totalTrfCount;
    }

    public int getTotalTrfTime() {
        int totalTrfTime = 0;
        for (PathSearchSegment pathSearchSegment : pathSearchSegments) {
            if (pathSearchSegment.isTrf()) {
                totalTrfTime += pathSearchSegment.getRequiredTime();
            }
        }
        return totalTrfTime;
    }

    public List<PathSearchSegmentDto.Response> toDto() {
        List<PathSearchSegmentDto.Response> pathSearchSegmentsDto = new ArrayList<>();
        pathSearchSegments.forEach(pathSearchSegment -> pathSearchSegmentsDto.add(new PathSearchSegmentDto.Response(pathSearchSegment)));
        return pathSearchSegmentsDto;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("총 소요시간(대기시간 포함): ").append(totalRequiredTime).append("분\n");
        sb.append("환승 횟수: ").append(getTotalTrfCnt()).append("회\n");
        sb.append("총 환승시간: ").append(getTotalTrfTime()).append("분\n");
        sb.append("혼잡도 점수: ").append(congestionScore).append("\n");
        sb.append("노선\t\t").append(StringUtil.equalizeStinNmLen("출발역")).append(StringUtil.equalizeStinNmLen("도착역")).append(StringUtil.equalizeStinNmLen("시간")).append("방향\n");
        sb.append("-".repeat(105)).append('\n');
        pathSearchSegments.forEach(sb::append);

        return sb.toString();
    }

    private void setStopoverInfo(String stopoverStinNm, int stopoverTime) {
        if (stopoverStinNm == null) {
            return;
        }
        for (int i = 0; i < pathSearchSegments.size(); i++) {
            PathSearchSegment pathSearchSegment = pathSearchSegments.get(i);
            if (!pathSearchSegment.isStopOverStin(stopoverStinNm)) {
                continue;
            }
            int trfTime = pathSearchSegment.getRequiredTime() - stopoverTime;
            pathSearchSegments.add(i, PathSearchSegment.of(
                    pathSearchSegment.getLnCd(),
                    pathSearchSegment.getOrgStinNm(),
                    pathSearchSegment.getDestStinNm(),
                    pathSearchSegment.getOrgTm(),
                    DateUtil.plusMinutes(pathSearchSegment.getOrgTm(), trfTime),
                    null,
                    null
            ));
            pathSearchSegments.add(i + 1, PathSearchSegment.of(
                    "경유",
                    pathSearchSegment.getOrgStinNm(),
                    pathSearchSegment.getDestStinNm(),
                    DateUtil.plusMinutes(pathSearchSegment.getOrgTm(), trfTime),
                    pathSearchSegment.getDestTm(),
                    null,
                    null
            ));
            pathSearchSegments.remove(i + 2);
            return;
        }
    }
}
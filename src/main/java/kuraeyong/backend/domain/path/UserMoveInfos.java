package kuraeyong.backend.domain.path;

import kuraeyong.backend.util.StringUtil;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.List;

@Getter
public class UserMoveInfos {
    @Getter(AccessLevel.NONE)
    private final List<UserMoveInfo> userMoveInfos;
    private final int totalRequiredTime;
    private final int congestionScore;

    public UserMoveInfos(List<UserMoveInfo> userMoveInfos, int totalRequiredTime, int congestionScore) {
        this.userMoveInfos = userMoveInfos;
        this.totalRequiredTime = totalRequiredTime;
        this.congestionScore = congestionScore;
    }

    public int getTotalTrfCnt() {
        int totalTrfCount = 0;
        for (UserMoveInfo userMoveInfo : userMoveInfos) {
            if (userMoveInfo.isTrf()) {
                totalTrfCount++;
            }
        }
        return totalTrfCount;
    }

    public int getTotalTrfTime() {
        int totalTrfTime = 0;
        for (UserMoveInfo userMoveInfo : userMoveInfos) {
            if (userMoveInfo.isTrf()) {
                totalTrfTime += userMoveInfo.getRequiredTime();
            }
        }
        return totalTrfTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("총 소요시간(대기시간 포함): ").append(totalRequiredTime).append("분\n");
        sb.append("환승 횟수: ").append(getTotalTrfCnt()).append("회\n");
        sb.append("총 환승시간: ").append(getTotalTrfTime()).append("분\n");
        sb.append("혼잡도 점수: ").append(congestionScore).append("\n");
        sb.append("노선\t\t").append(StringUtil.equalizeStinNmLen("출발역")).append(StringUtil.equalizeStinNmLen("도착역")).append("시간\n");
        sb.append("-".repeat(84)).append('\n');
        userMoveInfos.forEach(sb::append);

        return sb.toString();
    }
}

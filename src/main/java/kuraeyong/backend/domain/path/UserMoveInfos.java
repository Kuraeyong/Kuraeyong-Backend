package kuraeyong.backend.domain.path;

import kuraeyong.backend.dto.response.UserMoveInfoDto;
import kuraeyong.backend.util.DateUtil;
import kuraeyong.backend.util.StringUtil;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class UserMoveInfos {
    @Getter(AccessLevel.NONE)
    private final List<UserMoveInfo> userMoveInfos;
    private final int totalRequiredTime;
    private final int congestionScore;

    public UserMoveInfos(List<UserMoveInfo> userMoveInfos, int totalRequiredTime, int congestionScore, String stopoverStinNm, int stopoverTime) {
        this.userMoveInfos = userMoveInfos;
        this.totalRequiredTime = totalRequiredTime;
        this.congestionScore = congestionScore;
        setStopoverInfo(stopoverStinNm, stopoverTime);
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

    public List<UserMoveInfoDto> toDto() {
        List<UserMoveInfoDto> userMoveInfosDto = new ArrayList<>();
        userMoveInfos.forEach(userMoveInfo -> userMoveInfosDto.add(new UserMoveInfoDto(userMoveInfo)));
        return userMoveInfosDto;
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
        userMoveInfos.forEach(sb::append);

        return sb.toString();
    }

    private void setStopoverInfo(String stopoverStinNm, int stopoverTime) {
        if (stopoverStinNm == null) {
            return;
        }
        for (int i = 0; i < userMoveInfos.size(); i++) {
            UserMoveInfo userMoveInfo = userMoveInfos.get(i);
            if (!userMoveInfo.isStopOverStin(stopoverStinNm)) {
                continue;
            }
            userMoveInfos.add(i, UserMoveInfo.of(
                    "경유",
                    userMoveInfo.getOrgStinNm(),
                    userMoveInfo.getDestStinNm(),
                    userMoveInfo.getOrgTm(),
                    DateUtil.plusMinutes(userMoveInfo.getOrgTm(), stopoverTime),
                    null,
                    null
            ));
            userMoveInfos.add(i + 1, UserMoveInfo.of(
                    userMoveInfo.getLnCd(),
                    userMoveInfo.getOrgStinNm(),
                    userMoveInfo.getDestStinNm(),
                    DateUtil.plusMinutes(userMoveInfo.getOrgTm(), stopoverTime),
                    userMoveInfo.getDestTm(),
                    null,
                    null
            ));
            userMoveInfos.remove(i + 2);
            return;
        }
    }
}
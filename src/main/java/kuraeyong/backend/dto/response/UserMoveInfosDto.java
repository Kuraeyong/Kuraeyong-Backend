package kuraeyong.backend.dto.response;

import kuraeyong.backend.domain.path.UserMoveInfos;
import lombok.Getter;

import java.util.List;

@Getter
public class UserMoveInfosDto {
    private final int totalRequiredTime;
    private final int totalTrfCnt;
    private final int totalTrfTime;
    private final List<UserMoveInfoDto> userMoveInfos;

    public UserMoveInfosDto(UserMoveInfos userMoveInfos) {
        this.totalRequiredTime = userMoveInfos.getTotalRequiredTime();
        this.totalTrfCnt = userMoveInfos.getTotalTrfCnt();
        this.totalTrfTime = userMoveInfos.getTotalTrfTime();
        this.userMoveInfos = userMoveInfos.toDto();
    }
}

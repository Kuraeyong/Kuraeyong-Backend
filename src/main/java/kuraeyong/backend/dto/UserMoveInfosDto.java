package kuraeyong.backend.dto;

import kuraeyong.backend.domain.path.UserMoveInfos;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class UserMoveInfosDto {

    @AllArgsConstructor
    @Getter
    public static class Request {
        private String orgStinNm;
        private String stopoverStinNm;
        private String destStinNm;
        private String dateType;
        private int hour;
        private int min;
        private int congestionThreshold;
        private String convenience;
        private int stopoverTime;
        private String sortType;
    }

    @Getter
    public static class Response {
        private final int totalRequiredTime;
        private final int totalTrfCnt;
        private final int totalTrfTime;
        private final List<UserMoveInfoDto.Response> userMoveInfos;

        public Response(UserMoveInfos userMoveInfos) {
            this.totalRequiredTime = userMoveInfos.getTotalRequiredTime();
            this.totalTrfCnt = userMoveInfos.getTotalTrfCnt();
            this.totalTrfTime = userMoveInfos.getTotalTrfTime();
            this.userMoveInfos = userMoveInfos.toDto();
        }
    }
}

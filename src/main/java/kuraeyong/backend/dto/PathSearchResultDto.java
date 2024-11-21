package kuraeyong.backend.dto;

import kuraeyong.backend.domain.path.PathSearchResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class PathSearchResultDto {

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
        private int stopoverTime;
        private String sortType;
    }

    @Getter
    public static class Response {
        private final int totalRequiredTime;
        private final int totalTrfCnt;
        private final int totalTrfTime;
        private final List<PathSearchSegmentDto.Response> pathSearchSegments;

        public Response(PathSearchResult pathSearchResult) {
            this.totalRequiredTime = pathSearchResult.getTotalRequiredTime();
            this.totalTrfCnt = pathSearchResult.getTotalTrfCnt();
            this.totalTrfTime = pathSearchResult.getTotalTrfTime();
            this.pathSearchSegments = pathSearchResult.toDto();
        }
    }
}

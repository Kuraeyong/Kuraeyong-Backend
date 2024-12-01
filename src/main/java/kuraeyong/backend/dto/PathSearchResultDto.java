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
        private final int avgCongestion;
        private final int maxCongestion;
        private final List<PathSearchSegmentDto.Response> pathSearchSegmentsDto;

        public Response(PathSearchResult pathSearchResult) {
            this.totalRequiredTime = pathSearchResult.getTotalRequiredTime();
            this.totalTrfCnt = pathSearchResult.getTotalTrfCnt();
            this.totalTrfTime = pathSearchResult.getTotalTrfTime();
            this.avgCongestion = pathSearchResult.getAvgCongestion();
            this.maxCongestion = pathSearchResult.getMaxCongestion();
            this.pathSearchSegmentsDto = pathSearchResult.toDto();
            determineLnCdAndStinType();
        }

        private void determineLnCdAndStinType() {
            // 출발 포인트 설정
            PathSearchSegmentDto.Response first = pathSearchSegmentsDto.get(0);
            first.setOrgStinType("출발");
            // 중간 포인트 설정
            for (int i = 1; i < pathSearchSegmentsDto.size() - 1; i++) {
                PathSearchSegmentDto.Response prev = pathSearchSegmentsDto.get(i - 1);
                PathSearchSegmentDto.Response curr = pathSearchSegmentsDto.get(i);

                if (curr.getLnCd().equals("경유")) {
                    curr.setLnCd(null);
                    curr.setOrgStinType("경유");
                    continue;
                }
                if (curr.getLnCd().equals("환승")) {
                    curr.setLnCd(prev.getLnCd());
                    curr.setOrgStinType("환승시작");
                    continue;
                }
                if (prev.getOrgStinType().equals("경유") || prev.getOrgStinType().equals("환승시작")) {
                    curr.setOrgStinType("환승종료");
                }
            }
            // 도착 포인트 설정
            PathSearchSegmentDto.Response last = pathSearchSegmentsDto.get(pathSearchSegmentsDto.size() - 1);
            last.setOrgStinType("도착");
        }
    }
}

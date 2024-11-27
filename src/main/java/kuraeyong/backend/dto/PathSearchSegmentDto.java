package kuraeyong.backend.dto;

import kuraeyong.backend.domain.path.PathSearchSegment;
import kuraeyong.backend.util.DateUtil;
import lombok.Getter;
import lombok.Setter;

public class PathSearchSegmentDto {

    @Getter
    public static class Response {
        @Setter
        private String lnCd;
        @Setter
        private String orgStinType;
        private final String orgStinNm;
        private final String orgTime;
        private final int requiredTime;
        private final String trnTmnStinNm;
        private final String trnDir;

        public Response(PathSearchSegment pathSearchSegment) {
            this.lnCd = pathSearchSegment.getLnCd();
            this.orgStinType = null;
            this.orgStinNm = pathSearchSegment.getOrgStinNm();
            this.orgTime = pathSearchSegment.getOrgTm();
            this.requiredTime = DateUtil.getMinDiff(pathSearchSegment.getOrgTm(), pathSearchSegment.getDestTm());
            this.trnTmnStinNm = pathSearchSegment.getTrnTmnStinNm();
            this.trnDir = pathSearchSegment.getTrnDir();
        }
    }
}

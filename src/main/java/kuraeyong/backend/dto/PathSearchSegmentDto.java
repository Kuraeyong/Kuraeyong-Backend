package kuraeyong.backend.dto;

import kuraeyong.backend.domain.path.PathSearchSegment;
import lombok.Getter;

public class PathSearchSegmentDto {

    @Getter
    public static class Response {
        private final String lnCd;
        private final String orgStinNm;
        private final String destStinNm;
        private final String orgTm;
        private final String destTm;
        private final String trnTmnStinNm;
        private final String trnDir;

        public Response(PathSearchSegment pathSearchSegment) {
            this.lnCd = pathSearchSegment.getLnCd();
            this.orgStinNm = pathSearchSegment.getOrgStinNm();
            this.destStinNm = pathSearchSegment.getDestStinNm();
            this.orgTm = pathSearchSegment.getOrgTm();
            this.destTm = pathSearchSegment.getDestTm();
            this.trnTmnStinNm = pathSearchSegment.getTrnTmnStinNm();
            this.trnDir = pathSearchSegment.getTrnDir();
        }
    }
}

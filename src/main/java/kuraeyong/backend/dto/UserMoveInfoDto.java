package kuraeyong.backend.dto;

import kuraeyong.backend.domain.path.UserMoveInfo;
import lombok.Getter;

public class UserMoveInfoDto {

    @Getter
    public static class Response {
        private final String lnCd;
        private final String orgStinNm;
        private final String destStinNm;
        private final String orgTm;
        private final String destTm;
        private final String trnTmnStinNm;
        private final String trnDir;

        public Response(UserMoveInfo userMoveInfo) {
            this.lnCd = userMoveInfo.getLnCd();
            this.orgStinNm = userMoveInfo.getOrgStinNm();
            this.destStinNm = userMoveInfo.getDestStinNm();
            this.orgTm = userMoveInfo.getOrgTm();
            this.destTm = userMoveInfo.getDestTm();
            this.trnTmnStinNm = userMoveInfo.getTrnTmnStinNm();
            this.trnDir = userMoveInfo.getTrnDir();
        }
    }
}

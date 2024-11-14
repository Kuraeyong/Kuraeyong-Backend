package kuraeyong.backend.domain.path;

import kuraeyong.backend.util.DateUtil;
import kuraeyong.backend.util.StringUtil;
import lombok.Getter;

@Getter
public class UserMoveInfo {
    private final String lnCd;
    private final String orgStinNm;
    private final String destStinNm;
    private final String orgTm;
    private final String destTm;

    private UserMoveInfo(String lnCd, String orgStinNm, String destStinNm, String orgTm, String destTm) {
        this.lnCd = lnCd;
        this.orgStinNm = orgStinNm;
        this.destStinNm = destStinNm;
        this.orgTm = orgTm;
        this.destTm = destTm;
    }

    public static UserMoveInfo of(String lnCd, String orgStinNm, String destStinNm, String orgTm, String destTm) {
        return new UserMoveInfo(lnCd, orgStinNm, destStinNm, orgTm, destTm);
    }

    public int getRequiredTime() {
        return DateUtil.getMinDiff(orgTm, destTm);
    }

    public boolean isTrf() {
        return lnCd == null;
    }

    @Override
    public String toString() {
        return determineLnCd() + "\t\t" +
                StringUtil.equalizeStinNmLen(orgStinNm) + StringUtil.equalizeStinNmLen(destStinNm) +
                DateUtil.getMinDiff(orgTm, destTm) + "분" +
                "(" + orgTm + "~" + destTm + ")\n";
    }

    private String determineLnCd() {
        if (!isTrf()) {
            return lnCd;
        }
        return "환승";
    }
}


package kuraeyong.backend.domain.path;

import kuraeyong.backend.util.DateUtil;
import kuraeyong.backend.util.StringUtil;
import lombok.Getter;

@Getter
public class PathSearchSegment {
    private final String lnCd;
    private final String orgStinNm;
    private final String destStinNm;
    private final String orgTm;
    private final String destTm;
    private final String trnTmnStinNm;
    private final String trnDir;

    private PathSearchSegment(String lnCd, String orgStinNm, String destStinNm, String orgTm, String destTm, String trnTmnStinNm, String trnDir) {
        this.lnCd = lnCd;
        this.orgStinNm = orgStinNm;
        this.destStinNm = destStinNm;
        this.orgTm = orgTm;
        this.destTm = destTm;
        this.trnTmnStinNm = trnTmnStinNm;
        this.trnDir = trnDir;
    }

    public static PathSearchSegment of(String lnCd, String orgStinNm, String destStinNm, String orgTm, String destTm, String trnTmnStinNm, String trnDir) {
        return new PathSearchSegment(lnCd, orgStinNm, destStinNm, orgTm, destTm, trnTmnStinNm, trnDir);
    }

    public int getRequiredTime() {
        return DateUtil.getMinDiff(orgTm, destTm);
    }

    public boolean isTrf() {
        return lnCd == null;
    }

    public boolean isStopOverStin(String stopoverStinNm) {
        return orgStinNm.equals(stopoverStinNm) && destStinNm.equals(stopoverStinNm);
    }

    @Override
    public String toString() {
        return determineLnCd() + "\t\t" +
                StringUtil.equalizeStinNmLen(orgStinNm) + StringUtil.equalizeStinNmLen(destStinNm) +
                DateUtil.getMinDiff(orgTm, destTm) + "분" +
                "(" + orgTm + "~" + destTm + ")\t\t\t" +
                printDirection(trnTmnStinNm, trnDir) + "\n";
    }

    private String determineLnCd() {
        if (!isTrf()) {
            return lnCd;
        }
        return "환승";
    }

    private String printDirection(String trnTmnStinNm, String trnDir) {
        if (trnTmnStinNm == null || trnDir == null) {
            return "";
        }
        return trnTmnStinNm + " 방향(" + trnDir + ")";
    }
}


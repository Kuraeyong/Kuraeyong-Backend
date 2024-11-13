package kuraeyong.backend.domain.path;

import kuraeyong.backend.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PathResult implements Comparable<PathResult> {
    private final MetroPath path;
    private final MetroPath compressedPath;
    private final MoveInfos moveInfos;
    @Setter
    private int congestionScore;

    public PathResult(MetroPath path, MoveInfos moveInfos) {
        this.path = path;
        this.compressedPath = path.getCompressPath();
        this.moveInfos = moveInfos;
    }

    public List<MetroNodeWithEdge> getIterablePath() {
        return path.getPath();
    }

    public String getFinalArvTm() {
        return moveInfos.getArvTm(moveInfos.size() - 1);
    }

    public int getTotalTime() {
        return DateUtil.getMinDiff(moveInfos.getArvTm(0), moveInfos.getArvTm(moveInfos.size() - 1));
    }

    public int getTrfCnt() {
        return moveInfos.getTrfCnt();
    }

    public int getTotalTrfTime() {
        return moveInfos.getTotalTrfTime();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // compressedPath
        sb.append(path).append('\n');
        sb.append(compressedPath).append('\n');
        sb.append(moveInfos);

        // moveInfoList
        sb.append("총 소요시간(대기시간 포함): ").append(getTotalTime()).append("분\n");
        sb.append("환승 횟수: ").append(getTrfCnt()).append("회\n");
        sb.append("총 환승시간: ").append(getTotalTrfTime()).append("분\n");
        sb.append("혼잡도 점수: ").append(congestionScore).append("\n");
        sb.append("노선\t\t").append(equalizeStinNmLen("출발역")).append(equalizeStinNmLen("도착역")).append("시간\n");
        sb.append("-".repeat(84)).append('\n');

        int lnOrgIdx = 1;
        MoveInfo lnOrg;
        String orgDptTm, destArvTm;
        for (int i = 2; i < moveInfos.size(); i++) {
            MoveInfo prev = moveInfos.get(i - 1);
            MoveInfo curr = moveInfos.get(i);

            if (curr.getTrnGroupNo() == prev.getTrnGroupNo()) {
                continue;
            }
            // userMoveInfo 처리
            lnOrg = moveInfos.get(lnOrgIdx);
            orgDptTm = lnOrg.getDptTm();
            destArvTm = prev.getArvTm();
            appendUserMoveInfo(sb, lnOrg.getLnCd(), compressedPath.get(lnOrgIdx - 1).getStinNm(),
                    compressedPath.get(i - 1).getStinNm(), orgDptTm, destArvTm);
            lnOrgIdx = i;

            // 일반/급행 환승 관련 userMoveInfo 처리
            if (prev.getTrnGroupNo() != -1 && curr.getTrnGroupNo() != -1) {
                String stinNm = compressedPath.get(i - 1).getStinNm();
                String time = prev.getArvTm();
                appendUserMoveInfo(sb, null, stinNm, stinNm, time, time);
            }
        }
        // 마지막 userMoveInfo 처리
        lnOrg = moveInfos.get(lnOrgIdx);
        orgDptTm = lnOrg.getDptTm();
        destArvTm = moveInfos.getArvTm(moveInfos.size() - 1);
        appendUserMoveInfo(sb, lnOrg.getLnCd(), compressedPath.get(lnOrgIdx - 1).getStinNm(),
                compressedPath.get(compressedPath.size() - 1).getStinNm(), orgDptTm, destArvTm);
        sb.append('\n');

        return sb.toString();
    }

    private void appendUserMoveInfo(StringBuilder sb, String lnCd, String orgStinNm, String destStinNm, String orgDptTm, String destArvTm) {
        sb.append(lnCd == null ? "환승" : lnCd).append("\t\t")
                .append(equalizeStinNmLen(orgStinNm)).append(equalizeStinNmLen(destStinNm))
                .append(DateUtil.getMinDiff(orgDptTm, destArvTm)).append("분")
                .append("(").append(orgDptTm).append("~").append(destArvTm).append(")\n");
    }

    private String equalizeStinNmLen(String stinNm) {
        int tapCnt = switch (stinNm.length()) {
            case 1, 2 -> 7;
            case 3, 4 -> 6;
            case 5, 6, 7 -> 5;
            case 8, 9 -> 4;
            case 10, 11, 12 -> 3;
            case 13, 14 -> 2;
            default -> 1;
        };
        tapCnt = stinNm.matches(".*[0-9].*") ? tapCnt + 1 : tapCnt;

        StringBuilder sb = new StringBuilder(stinNm);
        while (tapCnt-- > 0) {
            sb.append("\t");
        }
        return sb.toString();
    }

    @Override
    public int compareTo(PathResult o) {
        if (!getFinalArvTm().equals(o.getFinalArvTm())) {    // 도착시간이 다르면
            return getFinalArvTm().compareTo(o.getFinalArvTm());
        }
        if (getTrfCnt() != o.getTrfCnt()) {    // 환승횟수가 다르면
            return Integer.compare(getTrfCnt(), o.getTrfCnt());
        }
        return Integer.compare(getTotalTrfTime(), o.getTotalTrfTime());
    }
}

package kuraeyong.backend.domain.constant;

import kuraeyong.backend.domain.path.MetroNodeWithEdge;

public enum BranchDirectionType {
    MAIN_TO_SUB("01"),    // 본선 -> 지선
    SUB_TO_MAIN("10");    // 지선 -> 본선

    private final String branchDirectionType;

    BranchDirectionType(String branchDirectionType) {
        this.branchDirectionType = branchDirectionType;
    }

    public String get() {
        return branchDirectionType;
    }

    public static BranchDirectionType convertToBranchDirectionType(String prevBranchInfo, String nextBranchInfo) {
        int prevVal = prevBranchInfo.charAt(prevBranchInfo.length() - 1) - '0';
        int nextVal = nextBranchInfo.charAt(nextBranchInfo.length() - 1) - '0';

        return (prevVal < nextVal) ? MAIN_TO_SUB : SUB_TO_MAIN;
    }

    public static boolean isBranchTrf(MetroNodeWithEdge prev, MetroNodeWithEdge curr, MetroNodeWithEdge next) {
        if (!curr.isJctStin()) {    // 분기점이 아니면, 분기점 환승 X
            return false;
        }
        if (prev.isDifferentLine(next.getLnCd())) {   // 노선도 다르면, 분기점 환승 X
            return false;
        }
        String prevBranchInfo = prev.getBranchInfo();
        String nextBranchInfo = next.getBranchInfo();
        if (prevBranchInfo.equals(nextBranchInfo)) {    // 값도 같으면, 분기점 환승 X
            return false;
        }
        if (prevBranchInfo.length() != nextBranchInfo.length()) {   // 길이도 다르면, 분기점 환승 X
            return false;
        }
        if (curr.getLnCd().equals("6")) {   // 6호선인 경우, 추가로 고려해야 함
            int prevBranchVal = Integer.parseInt(prevBranchInfo);
            int nextBranchVal = Integer.parseInt(nextBranchInfo);

            if (prevBranchVal < 1 || nextBranchVal < 1) {
                return false;
            }
            return prevBranchVal < nextBranchVal;
        }
        return true;
    }
}

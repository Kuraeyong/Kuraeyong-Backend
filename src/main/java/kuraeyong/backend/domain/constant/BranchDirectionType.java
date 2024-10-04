package kuraeyong.backend.domain.constant;

public enum BranchDirectionType {
    MAIN_TO_SUB("01"),    // 본선 -> 지선
    SUB_TO_MAIN("10");    // 지선 -> 본선

    private final String branchDirectionType;

    private BranchDirectionType(String branchDirectionType) {
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
}

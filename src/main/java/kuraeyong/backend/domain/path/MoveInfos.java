package kuraeyong.backend.domain.path;

import kuraeyong.backend.util.DateUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MoveInfos {
    @Setter(AccessLevel.NONE)
    private final List<MoveInfo> moveInfos;
    private int trfCnt;
    private int totalTrfTime;

    public MoveInfos() {
        moveInfos = new ArrayList<>();
    }

    public MoveInfos(MoveInfos moveInfos) {
        this.moveInfos = new ArrayList<>();
        for (MoveInfo moveInfo : moveInfos.getMoveInfos()) {
            add(new MoveInfo(moveInfo));
        }
        this.trfCnt = moveInfos.trfCnt;
        this.totalTrfTime = moveInfos.totalTrfTime;
    }

    public void concat(MoveInfos moveInfos, int stopoverTime) {
        // 첫번째 무프인포 연결
        MoveInfo moveInfo = new MoveInfo(moveInfos.get(0));
        moveInfo.setDptTm(get(size() - 1).getArvTm());
        moveInfo.setArvTm(moveInfos.get(1).getDptTm());
        if (isValidStopoverTime(stopoverTime)) {
            this.trfCnt++;
            this.totalTrfTime += DateUtil.getMinDiff(moveInfo.getArvTm(), moveInfo.getDptTm());
        }
        add(moveInfo);

        // 남은 무브인포 연결
        for (int i = 1; i < moveInfos.size(); i++) {
            moveInfo = new MoveInfo(moveInfos.get(i));
            if (moveInfo.getTrnGroupNo() != -1) {
                moveInfo.setTrnGroupNo(moveInfo.getTrnGroupNo() + moveInfos.trfCnt + 1);
            }
            add(moveInfo);
        }

        // 남은 필드 연결
        this.trfCnt += moveInfos.trfCnt;
        this.totalTrfTime += moveInfos.totalTrfTime;
    }

    /**
     * 경유역에서 환승을 하는 경우인지 검사
     *
     * @param stopoverTime 경유 시간
     * @return 경유역 환승 여부
     */
    private boolean isValidStopoverTime(int stopoverTime) {
        return stopoverTime != -1;
    }

    public void add(MoveInfo moveInfo) {
        moveInfos.add(moveInfo);
    }

    public int size() {
        return moveInfos.size();
    }

    public MoveInfo get(int idx) {
        return moveInfos.get(idx);
    }

    public String getArvTm(int idx) {
        return get(idx).getArvTm();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (MoveInfo moveInfo : moveInfos) {
            sb.append(moveInfo).append('\n');
        }
        return sb.toString();
    }
}

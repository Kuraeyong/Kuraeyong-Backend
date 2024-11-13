package kuraeyong.backend.domain.path;

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

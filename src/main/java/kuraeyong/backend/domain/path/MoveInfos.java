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
    private final List<MoveInfo> moveInfoList;
    private int trfCnt;
    private int totalTrfTime;

    public MoveInfos() {
        moveInfoList = new ArrayList<>();
    }

    public void add(MoveInfo moveInfo) {
        moveInfoList.add(moveInfo);
    }

    public int size() {
        return moveInfoList.size();
    }

    public MoveInfo get(int idx) {
        return moveInfoList.get(idx);
    }

    public String getArvTm(int idx) {
        return get(idx).getArvTm();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (MoveInfo moveInfo : moveInfoList) {
            sb.append(moveInfo).append('\n');
        }
        return sb.toString();
    }
}

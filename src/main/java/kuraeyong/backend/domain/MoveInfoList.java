package kuraeyong.backend.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MoveInfoList {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final List<MoveInfo> list;
    private int trfCnt;
    private int totalTrfTime;

    public MoveInfoList() {
        list = new ArrayList<>();
    }

    public void add(MoveInfo moveInfo) {
        list.add(moveInfo);
    }

    public int size() {
        return list.size();
    }

    public MoveInfo get(int idx) {
        return list.get(idx);
    }

    public String getFinalArvTm() {
        return get(size() - 1).getArvTm();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("trfCnt: ").append(trfCnt).append('\n');
        sb.append("totalTrfTime: ").append(totalTrfTime).append('\n');
        for (MoveInfo moveInfo : list) {
            sb.append(moveInfo).append('\n');
        }
        return sb.toString();
    }
}

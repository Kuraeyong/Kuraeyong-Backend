package kuraeyong.backend.domain;

import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class MoveInfoList {
    private final List<MoveInfo> list;
    @Setter
    private int trfCnt;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("trfCnt: ").append(trfCnt).append('\n');
        for (MoveInfo moveInfo : list) {
            sb.append(moveInfo).append('\n');
        }
        return sb.toString();
    }
}

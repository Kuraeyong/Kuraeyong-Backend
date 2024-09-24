package kuraeyong.backend.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Getter
public class TrnNoStdStationTimeTable extends StationTimeTable {
    private final HashMap<String, Integer> weekdaySameTrainMap;
    private final HashMap<String, Integer> holidaySameTrainMap;

    public TrnNoStdStationTimeTable() {
        super();
        weekdaySameTrainMap = new HashMap<>();
        holidaySameTrainMap = new HashMap<>();
    }

    public HashMap<String, Integer> getSameTrainMap(String dateType) {
        return switch (dateType) {
            case "평일" -> weekdaySameTrainMap;
            case "토", "휴일" -> holidaySameTrainMap;
            default -> null;
        };
    }

    public void printByGroupNumber(boolean isWeekday) {
        HashMap<String, Integer> sameTrainMap = (isWeekday) ? weekdaySameTrainMap : holidaySameTrainMap;
        HashMap<Integer, List<String>> trainGroupMap = new HashMap<>();

        Set<String> keySet = sameTrainMap.keySet();
        for (String key : keySet) {
            int groupNum = sameTrainMap.get(key);

            if (!trainGroupMap.containsKey(groupNum)) {
                trainGroupMap.put(groupNum, new ArrayList<>());
            }
            trainGroupMap.get(groupNum).add(key);
        }

        StringBuilder sb = new StringBuilder();
        Set<Integer> groupNumberSet = trainGroupMap.keySet();
        for (int groupNumber : groupNumberSet) {
            List<String> group = trainGroupMap.get(groupNumber);

            sb.append("[groupNumber: ").append(groupNumber).append("(").append(group.size()).append(")").append("]\n");
            for (String trainNumber : group) {
                sb.append(trainNumber).append('\n');
            }
            sb.append('\n');
        }
        System.out.println(sb);
    }
}
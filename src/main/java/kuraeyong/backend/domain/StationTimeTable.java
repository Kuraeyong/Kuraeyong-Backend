package kuraeyong.backend.domain;

import kuraeyong.backend.util.DateUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class StationTimeTable {
    private final List<StationTimeTableElement> list;

    public StationTimeTable() {
        list = new ArrayList<>();
    }

    public void add(StationTimeTableElement stationTimeTableElement) {
        list.add(stationTimeTableElement);
    }

    public void sort() {
        Collections.sort(list);
    }

    public int size() {
        return list.size();
    }

    public StationTimeTableElement get(int idx) {
        return list.get(idx);
    }

    /**
     * 특정시간(time) 이후에 도착하는 열차 목록 조회
     */
    public List<StationTimeTableElement> findByDptTmGreaterThanEqual(String time) {
        int timeVal = DateUtil.getTimeForCompare(time);
        for (int i = 0; i < size(); i++) {
            StationTimeTableElement train = get(i);

            // 종점이면 출발시간(dptTm)이 null이므로 도착시간(arvTm)을 기준으로 비교
            String dptTm = train.isTmnStin() ? train.getArvTm() : train.getDptTm();
            int dptTmVal = DateUtil.getTimeForCompare(dptTm);
            if (dptTmVal >= timeVal) {
                return list.subList(i, size());
            }
        }
        return null;
    }

    /**
     * 특정시간(time) 이후에 이 열차(trnNo)가 해당 역에 도착하는지 조회 (도착하면 열차 반환)
     */
    public StationTimeTableElement getStoppingTrainAfterCurrTime(String trnNo, String time) {
        List<StationTimeTableElement> trainList = findByDptTmGreaterThanEqual(time);
        if (trainList == null) {
            return null;
        }

        for (StationTimeTableElement train : trainList) {
            if (train.getTrnNo().equals(trnNo)) {
                return train;
            }
        }
        return null;
    }
}
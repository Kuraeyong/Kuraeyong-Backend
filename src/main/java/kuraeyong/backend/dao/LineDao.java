package kuraeyong.backend.dao;

import kuraeyong.backend.dto.station.GetLineListResponse;
import kuraeyong.backend.dto.station.GetListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LineDao {
    public GetLineListResponse getLineListByLineName(String lineName) {
        log.info("[LineDao.getLineListByLineName]");

        // 임시 코드
        List<List<String>> list = new ArrayList<>();
        list.add(new ArrayList<>(Arrays.asList("시청", "을지로입구", "을지로3가", "...", "아현", "충정로")));
        list.add(new ArrayList<>(Arrays.asList("성수", "용답", "신답", "용두", "신설동")));
        list.add(new ArrayList<>(Arrays.asList("신도림", "도림천", "양천구청", "신정네거리", "까치산")));

        return new GetLineListResponse(list);
    }
}

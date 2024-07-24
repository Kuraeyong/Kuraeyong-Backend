package kuraeyong.backend.dao;

import kuraeyong.backend.dto.line.GetLineListResponse;
import kuraeyong.backend.dto.line.GetStationInfoResponse;
import kuraeyong.backend.dto.line.GetStationTimeTableResponse;
import kuraeyong.backend.object.Position;
import kuraeyong.backend.object.StationInfoLineListElement;
import kuraeyong.backend.object.StationTimeTableLineListElement;
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

    public GetStationInfoResponse getStationInfo(String lineName, String stationName) {
        log.info("[LineDao.getStationInfo]");

        // 임시 코드
        String stationId = "212";
        String line = "2";
        String prev = "성수";
        String curr = "건대입구";
        String next = "뚝섬";
        List<StationInfoLineListElement> upLineList = new ArrayList<>(Arrays.asList(
                new StationInfoLineListElement("성수(외선행)", "곧 도착"),
                new StationInfoLineListElement("성수(외선행)", "4분 44초")
        ));
        List<StationInfoLineListElement> downLineList = new ArrayList<>(Arrays.asList(
                new StationInfoLineListElement("성수(내선행)", "1분 42초"),
                new StationInfoLineListElement("성수(내선행)", "7분 34초")
        ));
        Position pos = new Position("37.540408", "127.069231");
        List<String> facilityList = new ArrayList<>(Arrays.asList("엘리베이터", "자전거보관소", "무인민원발급기", "고객안내센터",
                "휠체어", "관광안내소", "만남의장소"));

        return new GetStationInfoResponse(stationId, line, prev, curr, next, upLineList, downLineList, pos, facilityList);
    }

    public GetStationTimeTableResponse getStationTimeTable(String lineName, String stationName, String dayType) {
        log.info("[LineDao.getStationTimeTable]");

        // 임시 코드
        List<StationTimeTableLineListElement> upLineList = new ArrayList<>(Arrays.asList(
                new StationTimeTableLineListElement("삼성", "성수", "05:45"),
                new StationTimeTableLineListElement("서울대입구", "성수", "06:05"),
                new StationTimeTableLineListElement("...", "...", "..."),
                new StationTimeTableLineListElement("성수", "성수", "24:58")
        ));
        List<StationTimeTableLineListElement> downLineList = new ArrayList<>(Arrays.asList(
                new StationTimeTableLineListElement("성수", "성수", "05:32"),
                new StationTimeTableLineListElement("성수", "성수", "05:42"),
                new StationTimeTableLineListElement("...", "...", "..."),
                new StationTimeTableLineListElement("성수", "삼성", "24:46")
        ));

        return new GetStationTimeTableResponse(upLineList, downLineList);
    }
}

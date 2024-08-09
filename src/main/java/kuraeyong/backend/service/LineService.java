package kuraeyong.backend.service;

import kuraeyong.backend.dto.element.Position;
import kuraeyong.backend.dto.element.StationInfoLineListElement;
import kuraeyong.backend.dto.element.StationTimeTableLineListElement;
import kuraeyong.backend.dto.response.GetListResponse;
import kuraeyong.backend.dto.response.line.GetLineListResponse;
import kuraeyong.backend.dto.response.line.GetStationInfoResponse;
import kuraeyong.backend.dto.response.line.GetStationTimeTableResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LineService {

    public GetLineListResponse getLineListByLineName(String lineName) {
        log.info("[LineService.getLineListByLineName]");

        // TODO: 해당 노선 리스트 조회 (지선이 있는 경우, 여러 노선이 조회될 수 있어서 리스트)
        // 임시 코드
        List<List<String>> list = new ArrayList<>();
        list.add(new ArrayList<>(Arrays.asList("시청", "을지로입구", "을지로3가", "...", "아현", "충정로")));
        list.add(new ArrayList<>(Arrays.asList("성수", "용답", "신답", "용두", "신설동")));
        list.add(new ArrayList<>(Arrays.asList("신도림", "도림천", "양천구청", "신정네거리", "까치산")));

        return new GetLineListResponse(list);
    }

    public GetStationInfoResponse getStationInfo(String lineName, String stationName) {
        log.info("[LineService.getStationInfo]");

        // TODO: 해당 역 정보 조회
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
        int exitCount = 4;
        List<String> facilityList = new ArrayList<>(Arrays.asList("엘리베이터", "자전거보관소", "무인민원발급기", "고객안내센터",
                "휠체어", "관광안내소", "만남의장소"));

        return new GetStationInfoResponse(stationId, line, prev, curr, next, upLineList, downLineList, pos, exitCount, facilityList);
    }

    public GetStationTimeTableResponse getStationTimeTable(String lineName, String stationName, String dayType) {
        log.info("[LineService.getStationTimeTable]");

        // TODO: 해당 역사 시간표 조회
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

    public GetListResponse getPlaceAroundExitList(String lineName, String stationName, String exitNumber) {
        log.info("[LineService.getPlaceAroundExitList]");

        // TODO: 해당 역 출구 주요장소 조회
        // 임시 코드
        List<String> list = new ArrayList<>(Arrays.asList(
                "롯데캐슬", "아이파크", "성락성결교회", "성수2,3동주민센터",
                "성수2가3파출소", "성수쇼핑센터", "성수지구대", "우방아파트",
                "향림공원", "성수수제화타운"
        ));

        return new GetListResponse(list);
    }
}

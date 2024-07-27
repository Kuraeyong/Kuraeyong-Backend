package kuraeyong.backend.service;

import kuraeyong.backend.dao.LineDao;
import kuraeyong.backend.dto.response.GetListResponse;
import kuraeyong.backend.dto.response.line.GetLineListResponse;
import kuraeyong.backend.dto.response.line.GetStationInfoResponse;
import kuraeyong.backend.dto.response.line.GetStationTimeTableResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LineService {

    private final LineDao lineDao;

    public GetLineListResponse getLineListByLineName(String lineName) {
        log.info("[LineService.getLineListByLineName]");

        // TODO: 해당 노선 리스트 조회 (지선이 있는 경우, 여러 노선이 조회될 수 있어서 리스트)
        return lineDao.getLineListByLineName(lineName);
    }

    public GetStationInfoResponse getStationInfo(String lineName, String stationName) {
        log.info("[LineService.getStationInfo]");

        // TODO: 해당 역 정보 조회
        return lineDao.getStationInfo(lineName, stationName);
    }

    public GetStationTimeTableResponse getStationTimeTable(String lineName, String stationName, String dayType) {
        log.info("[LineService.getStationTimeTable]");

        // TODO: 해당 역사 시간표 조회
        return lineDao.getStationTimeTable(lineName, stationName, dayType);
    }

    public GetListResponse getPlaceAroundExitList(String lineName, String stationName, String exitNumber) {
        log.info("[LineService.getPlaceAroundExitList]");

        // TODO: 해당 역 출구 주요장소 조회
        return lineDao.getPlaceAroundExitList(lineName, stationName, exitNumber);
    }
}

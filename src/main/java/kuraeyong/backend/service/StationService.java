package kuraeyong.backend.service;

import kuraeyong.backend.dao.StationDao;
import kuraeyong.backend.domain.Station;
import kuraeyong.backend.dto.response.GetListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationService {

    private final StationDao stationDao;

    public String createStationDB() {
        // TODO: Station DB 생성 및 초기화
        return stationDao.initStationDB();
    }

//    public GetListResponse getLineNameListByStationName(String stationName) {
//        log.info("[StationService.getLineNameListByStationName]");
//
//        // TODO: 해당 역이 속한 노선명 리스트 조회
//        return stationDao.getLineNameListByStationName(stationName);
//    }
}

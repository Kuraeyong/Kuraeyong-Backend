package kuraeyong.backend.service;

import kuraeyong.backend.dao.StationDao;
import kuraeyong.backend.dto.station.GetLineNameListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationService {

    private final StationDao stationDao;

    public GetLineNameListResponse getLineNameListByStationName(String stationName) {
        log.info("[StationService.getLineNameListByStationName]");

        // TODO: 해당 역이 속한 노선명 리스트 조회
        return stationDao.getLineNameListByStationName(stationName);
    }
}

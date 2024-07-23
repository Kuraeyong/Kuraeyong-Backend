package kuraeyong.backend.service;

import kuraeyong.backend.dao.StationDao;
import kuraeyong.backend.dto.station.GetLineListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationService {

    private final StationDao stationDao;

    public GetLineListResponse getLineListByStationName(String stationName) {
        log.info("[UserService.getAllUsers]");

        // TODO: 전체 회원 조회
        return stationDao.getLineListByStationName(stationName);
    }
}

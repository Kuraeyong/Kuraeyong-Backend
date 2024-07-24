package kuraeyong.backend.service;

import kuraeyong.backend.dao.LineDao;
import kuraeyong.backend.dao.StationDao;
import kuraeyong.backend.dto.station.GetLineListResponse;
import kuraeyong.backend.dto.station.GetListResponse;
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
}

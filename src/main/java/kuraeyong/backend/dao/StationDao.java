package kuraeyong.backend.dao;

import kuraeyong.backend.dao.repository.StationRepository;
import kuraeyong.backend.dto.response.GetListResponse;
import kuraeyong.backend.domain.Station;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class StationDao {

    @Autowired
    StationRepository stationRepository;

    public GetListResponse getLineNameListByStationName(String stationName) {
        log.info("[StationDao.getLineNameListByStationName]");

        List<String> list = new ArrayList<>();
        for (Station station : stationRepository.findAllByName(stationName)) {
            list.add(station.getLine());
        }

        return new GetListResponse(list);
    }
}

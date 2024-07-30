package kuraeyong.backend.dao;

import kuraeyong.backend.dao.repository.StationRepository;
import kuraeyong.backend.dto.response.GetListResponse;
import kuraeyong.backend.domain.Station;
import kuraeyong.backend.util.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class StationDao {

    @Autowired
    private StationRepository stationRepository;

    public String initStationDB() {
        List<Station> stationList = ExcelUtil.getStationListFromExcel("src/main/resources/xlsx/station_code_info.xlsx");
        stationRepository.deleteAll();
        List<Station> saveResult = stationRepository.saveAll(stationList);
        if (saveResult.size() == stationList.size()) {
            return "SUCCESS";
        }
        return "FAILED";
    }

    public List<Station> getStationList() {
        return stationRepository.findAll();
    }

//    public GetListResponse getLineNameListByStationName(String stationName) {
//        log.info("[StationDao.getLineNameListByStationName]");
//
//        List<String> list = new ArrayList<>();
//        // 추후 수정
////        for (Station station : stationRepository.findAllByName(stationName)) {
////            list.add(station.getLine());
////        }
//
//        return new GetListResponse(list);
//    }
}

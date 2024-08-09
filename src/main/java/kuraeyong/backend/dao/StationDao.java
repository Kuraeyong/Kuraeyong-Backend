package kuraeyong.backend.dao;

import kuraeyong.backend.dao.repository.StationInfoRepository;
import kuraeyong.backend.domain.StationInfo;
import kuraeyong.backend.util.FlatFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class StationDao {

    @Autowired
    private StationInfoRepository stationInfoRepository;

    public String initStationDB() {
        List<StationInfo> stationInfoList = FlatFileUtil.getStationListFromExcel("src/main/resources/xlsx/station_code_info.xlsx");
        stationInfoRepository.deleteAll();
        List<StationInfo> saveResult = stationInfoRepository.saveAll(stationInfoList);
        if (saveResult.size() == stationInfoList.size()) {
            return "SUCCESS";
        }
        return "FAILED";
    }

    public List<StationInfo> getStationList() {
        return stationInfoRepository.findAll();
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

package kuraeyong.backend.dao;

import kuraeyong.backend.dto.GetListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class StationDao {
    public GetListResponse getLineNameListByStationName(String stationName) {
        log.info("[StationDao.getLineNameListByStationName]");

        // 임시 코드
        List<String> list = new ArrayList<>();
        list.add("2");
        list.add("5");
        list.add("경의중앙");
        list.add("수인분당");

        return new GetListResponse(list);
    }
}

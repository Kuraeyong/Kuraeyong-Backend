package kuraeyong.backend.service;

import kuraeyong.backend.domain.constant.ConvenienceType;
import kuraeyong.backend.domain.station.convenience.StationConvenienceMap;
import kuraeyong.backend.dto.response.GetListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConvenienceService {
    private final StationConvenienceMap stationConvenienceMap;

    public GetListResponse getConvenienceStationList(ConvenienceType convenienceType) {
        return new GetListResponse(stationConvenienceMap.getConvenienceStationList(convenienceType));
    }
}

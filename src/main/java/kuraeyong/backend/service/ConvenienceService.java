package kuraeyong.backend.service;

import kuraeyong.backend.common.exception.ErrorMessage;
import kuraeyong.backend.domain.constant.ConvenienceType;
import kuraeyong.backend.domain.station.convenience.StationConvenienceMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConvenienceService {

    private final StationConvenienceMap stationConvenienceMap;

    /**
     * 해당 편의시설을 보유한 역 목록을 반환
     *
     * @param convenienceName 편의시설 이름
     * @return 해당 편의시설을 보유한 역 목록
     */
    public List<String> getStationsContainingConvenience(String convenienceName) {
        ConvenienceType convenienceType = ConvenienceType.parse(convenienceName);
        if (convenienceType == null) {
            throw new IllegalArgumentException(ErrorMessage.CONVENIENCE_NOT_FOUND.get());
        }
        return stationConvenienceMap.getStationsContainingConvenience(convenienceType);
    }
}

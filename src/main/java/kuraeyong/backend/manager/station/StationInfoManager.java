package kuraeyong.backend.manager.station;

import kuraeyong.backend.common.exception.ErrorMessage;
import kuraeyong.backend.domain.station.info.MinimumStationInfo;
import kuraeyong.backend.domain.station.info.StationInfo;
import kuraeyong.backend.repository.StationInfoRepository;
import kuraeyong.backend.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StationInfoManager implements StationDBInitializer {
    private final StationInfoRepository stationInfoRepository;

    @Override
    public boolean initDB(List<List<String>> rowList) {
        List<StationInfo> stationInfoList = Converter.toStationInfos(rowList);

        stationInfoRepository.deleteAll();
        return stationInfoList.size() == stationInfoRepository.saveAll(stationInfoList).size();
    }

    public MinimumStationInfo getStationByName(String stinNm) {
        List<StationInfo> stationInfos = stationInfoRepository.findByStinNm(stinNm);
        if (stationInfos.isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.STATION_NOT_FOUND.get());
        }
        StationInfo row = stationInfos.get(0);
        return MinimumStationInfo.build(row.getRailOprIsttCd(), row.getLnCd(), row.getStinCd());
    }

    public List<StationInfo> findAll() {
        return stationInfoRepository.findAll();
    }
}

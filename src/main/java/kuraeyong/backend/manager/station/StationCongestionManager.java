package kuraeyong.backend.manager.station;

import kuraeyong.backend.domain.station.congestion.StationCongestion;
import kuraeyong.backend.repository.StationCongestionRepository;
import kuraeyong.backend.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StationCongestionManager implements StationDBInitializer {
    private final StationCongestionRepository stationCongestionRepository;

    @Override
    public boolean initDB(List<List<String>> rowList) {
        List<StationCongestion> stationCongestionList = Converter.toStationCongestions(rowList);

        stationCongestionRepository.deleteAll();
        return stationCongestionList.size() == stationCongestionRepository.saveAll(stationCongestionList).size();
    }

    public List<StationCongestion> findAll() {
        return stationCongestionRepository.findAll();
    }
}

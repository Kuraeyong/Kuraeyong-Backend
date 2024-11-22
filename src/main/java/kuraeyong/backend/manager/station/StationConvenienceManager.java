package kuraeyong.backend.manager.station;

import kuraeyong.backend.domain.station.convenience.StationConvenience;
import kuraeyong.backend.repository.StationConvenienceRepository;
import kuraeyong.backend.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StationConvenienceManager implements StationDBInitializer {
    private final StationConvenienceRepository stationConvenienceRepository;

    @Override
    public boolean initDB(List<List<String>> rowList) {
        List<StationConvenience> stationConvenienceList = Converter.toStationConveniences(rowList);

        stationConvenienceRepository.deleteAll();
        return stationConvenienceList.size() == stationConvenienceRepository.saveAll(stationConvenienceList).size();
    }

    public List<StationConvenience> findAll() {
        return stationConvenienceRepository.findAll();
    }
}

package kuraeyong.backend.manager.station;

import kuraeyong.backend.domain.station.trf_weight.StationTrfWeight;
import kuraeyong.backend.repository.StationTrfWeightRepository;
import kuraeyong.backend.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StationTrfWeightManager implements StationDBInitializer {
    private final StationTrfWeightRepository stationTrfWeightRepository;

    @Override
    public boolean initDB(List<List<String>> rowList) {
        List<StationTrfWeight> stationTrfWeightList = Converter.toStationTrfWeights(rowList);

        stationTrfWeightRepository.deleteAll();
        return stationTrfWeightList.size() == stationTrfWeightRepository.saveAll(stationTrfWeightList).size();
    }
}

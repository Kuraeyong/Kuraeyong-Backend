package kuraeyong.backend.controller;

import kuraeyong.backend.domain.constant.FileType;
import kuraeyong.backend.service.StationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stations")
public class StationController {

    private final StationService stationService;

    @GetMapping("/init/station-info")
    public String initStationInfoDB() {
        return stationService.initDB(FileType.STATION_INFO);
    }

    @GetMapping("/init/station-trf-weight")
    public String initStationTrfWeightDB() {
        return stationService.initDB(FileType.STATION_TRF_WEIGHT);
    }

    @GetMapping("/init/station-congestion")
    public String initStationCongestionDB() {
        return stationService.initDB(FileType.STATION_CONGESTION);
    }

    @GetMapping("/init/station-convenience")
    public String initStationConvenienceDB() {
        return stationService.initDB(FileType.STATION_CONVENIENCE);
    }

    @GetMapping("/init/edge-info")
    public String initEdgeInfoDB() {
        return stationService.initDB(FileType.EDGE_INFO);
    }

    @GetMapping("/save/station-time-table-result")
    public String saveStationTimeTableAPIResultToCsv() {
        return stationService.saveStationTimeTableAPIResultToCsv();
    }

    @GetMapping("/load-csv")
    public String loadCsv() {
        return stationService.loadCsv();
    }
}

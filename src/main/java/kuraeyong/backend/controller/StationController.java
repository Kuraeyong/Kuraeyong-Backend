package kuraeyong.backend.controller;

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
//    @GetMapping("/{stationName}")
//    public BaseResponse<GetListResponse> getLineNameListByStationName(@PathVariable String stationName) {
//        log.info("[StationController.getLineNameListByStationName]");
//
//        return new BaseResponse<>(stationService.getLineNameListByStationName(stationName));
//    }

    @GetMapping("/init-db")
    public String initStationDB() {
        return stationService.createStationDB();
    }

    @GetMapping("/save-to-csv")
    public String saveApiResultToCsv() {
        stationService.saveApiResultToCsv();
        return "hi";
    }

    @GetMapping("/load-csv")
    public String loadCsv() {
        stationService.loadCsv();
        return "hi";
    }
}

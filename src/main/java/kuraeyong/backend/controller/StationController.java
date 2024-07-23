package kuraeyong.backend.controller;

import kuraeyong.backend.common.response.BaseResponse;
import kuraeyong.backend.dto.station.GetLineListResponse;
import kuraeyong.backend.service.StationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stations")
public class StationController {

    private final StationService stationService;
    @GetMapping("/{stationName}")
    public BaseResponse<GetLineListResponse> getLineListByStationName(@PathVariable String stationName) {
        log.info("[StationController.getLineListByStationName]");

        return new BaseResponse<>(stationService.getLineListByStationName(stationName));
    }
}

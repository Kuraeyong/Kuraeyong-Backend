package kuraeyong.backend.controller;

import kuraeyong.backend.common.response.BaseResponse;
import kuraeyong.backend.dto.GetListResponse;
import kuraeyong.backend.service.StationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stations")
public class StationController {

    private final StationService stationService;
    @GetMapping("/{stationName}")
    public BaseResponse<GetListResponse> getLineNameListByStationName(@PathVariable String stationName) {
        log.info("[StationController.getLineNameListByStationName]");

        return new BaseResponse<>(stationService.getLineNameListByStationName(stationName));
    }
}

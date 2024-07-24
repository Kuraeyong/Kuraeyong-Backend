package kuraeyong.backend.controller;

import kuraeyong.backend.common.response.BaseResponse;
import kuraeyong.backend.dto.station.GetLineListResponse;
import kuraeyong.backend.dto.station.GetListResponse;
import kuraeyong.backend.service.LineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lines")
public class LineController {

    private final LineService lineService;
    @GetMapping("/{lineName}")
    public BaseResponse<GetLineListResponse> getLineListByLineName(@PathVariable String lineName) {
        log.info("[LineController.getLineListByLineName]");

        return new BaseResponse<>(lineService.getLineListByLineName(lineName));
    }
}

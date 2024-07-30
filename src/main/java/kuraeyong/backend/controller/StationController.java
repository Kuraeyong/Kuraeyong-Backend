package kuraeyong.backend.controller;

import kuraeyong.backend.common.response.BaseResponse;
import kuraeyong.backend.dto.response.GetListResponse;
import kuraeyong.backend.service.StationService;
import kuraeyong.backend.util.ExcelUtil;
import kuraeyong.backend.util.OpenApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

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
}

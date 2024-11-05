package kuraeyong.backend.controller;

import kuraeyong.backend.domain.constant.ConvenienceType;
import kuraeyong.backend.dto.response.GetListResponse;
import kuraeyong.backend.service.ConvenienceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/conveniences")
public class ConvenienceController {

    private final ConvenienceService convenienceService;

    @GetMapping("/{convenienceName}")
    public GetListResponse getConvenienceStationList(@PathVariable String convenienceName) {
        return convenienceService.getConvenienceStationList(ConvenienceType.toConvenienceType(convenienceName));
    }
}

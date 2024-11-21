package kuraeyong.backend.controller;

import kuraeyong.backend.common.response.BaseResponse;
import kuraeyong.backend.common.response.ResponseStatus;
import kuraeyong.backend.service.ConvenienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/conveniences")
public class ConvenienceController {

    private final ConvenienceService convenienceService;

    @GetMapping("/{convenienceName}")
    public ResponseStatus findStationsByConvenienceName(@PathVariable String convenienceName) {
        return new BaseResponse<>(convenienceService.getStationsContainingConvenience(convenienceName));
    }
}

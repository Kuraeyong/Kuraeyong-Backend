package kuraeyong.backend.controller;

import kuraeyong.backend.common.response.BaseResponse;
import kuraeyong.backend.dto.response.train.GetTrainInfoResponse;
import kuraeyong.backend.service.TrainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trains")
public class TrainController {

    private final TrainService trainService;

    @GetMapping("/{trainId}")
    public BaseResponse<GetTrainInfoResponse> getTrainInfo(@PathVariable String trainId) {
        log.info("[TrainController.getTrainInfo]");

        return new BaseResponse<>(trainService.getTrainInfo(trainId));
    }
}

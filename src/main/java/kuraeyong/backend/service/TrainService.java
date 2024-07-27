package kuraeyong.backend.service;

import kuraeyong.backend.dao.TrainDao;
import kuraeyong.backend.dto.response.train.GetTrainInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainService {

    private final TrainDao trainDao;

    public GetTrainInfoResponse getTrainInfo(String trainId) {
        log.info("[TrainService.getTrainInfo]");

        // TODO: 해당 열차 정보 조회
        return trainDao.getTrainInfo(trainId);
    }
}

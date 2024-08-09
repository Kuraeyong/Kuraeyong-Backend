package kuraeyong.backend.service;

import kuraeyong.backend.dto.element.TrainInfoListElement;
import kuraeyong.backend.dto.response.train.GetTrainInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainService {

    public GetTrainInfoResponse getTrainInfo(String trainId) {
        log.info("[TrainService.getTrainInfo]");

        // TODO: 해당 열차 정보 조회
        // 임시 코드
        String dayType = "DAY";
        String direction = "IN";
        int isExpress = 0;
        String departmentStation = "성수";
        String arrivalStation = "성수";
        List<TrainInfoListElement> list = new ArrayList<>(Arrays.asList(
                new TrainInfoListElement("성수", "없음", "5:30:00"),
                new TrainInfoListElement("건대입구", "5:31:30", "5:32:00"),
                new TrainInfoListElement("구의", "5:34:00", "5:34:30"),
                new TrainInfoListElement("...", "...", "..."),
                new TrainInfoListElement("뚝섬", "6:57:30", "6:58:00"),
                new TrainInfoListElement("성수", "6:59:00", "없음")
        ));

        return new GetTrainInfoResponse(dayType, direction, isExpress, departmentStation, arrivalStation, list);
    }
}

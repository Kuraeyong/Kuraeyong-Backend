package kuraeyong.backend.dao;

import kuraeyong.backend.dto.response.train.GetTrainInfoResponse;
import kuraeyong.backend.dto.element.TrainInfoListElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TrainDao {
    public GetTrainInfoResponse getTrainInfo(String trainId) {
        log.info("[TrainDao.getTrainInfo]");

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

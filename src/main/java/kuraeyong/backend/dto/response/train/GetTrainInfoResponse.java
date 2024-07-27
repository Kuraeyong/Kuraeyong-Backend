package kuraeyong.backend.dto.response.train;

import kuraeyong.backend.dto.element.TrainInfoListElement;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetTrainInfoResponse {
    private String day_type;
    private String direction;
    private int is_express;
    private String department_station;
    private String arrival_station;
    private List<TrainInfoListElement> list;
}

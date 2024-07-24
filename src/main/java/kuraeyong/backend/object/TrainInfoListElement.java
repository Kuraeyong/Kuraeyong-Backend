package kuraeyong.backend.object;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TrainInfoListElement {
    private String station;
    private String arrival_time;
    private String department_time;
}

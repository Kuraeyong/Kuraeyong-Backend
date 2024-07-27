package kuraeyong.backend.dto.element;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StationTimeTableLineListElement {
    private String depart;
    private String dest;
    private String departure_time;
}

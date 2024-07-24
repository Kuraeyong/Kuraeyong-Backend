package kuraeyong.backend.object;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StationTimeTableLineListElement {
    private String depart;
    private String dest;
    private String departure_time;
}

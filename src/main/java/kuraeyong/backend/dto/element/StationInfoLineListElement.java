package kuraeyong.backend.dto.element;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StationInfoLineListElement {
    private String dest;
    private String arrival_time;
}

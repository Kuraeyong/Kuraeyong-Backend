package kuraeyong.backend.dto.response.line;

import kuraeyong.backend.dto.element.Position;
import kuraeyong.backend.dto.element.StationInfoLineListElement;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetStationInfoResponse {
    private String stationId;
    private String line;
    private String prev;
    private String curr;
    private String next;
    private List<StationInfoLineListElement> upLineList;
    private List<StationInfoLineListElement> downLineList;
    private Position pos;
    private int exitCount;
    private List<String> facilityList;
}

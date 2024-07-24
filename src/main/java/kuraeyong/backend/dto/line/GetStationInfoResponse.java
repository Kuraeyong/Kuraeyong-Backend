package kuraeyong.backend.dto.line;

import kuraeyong.backend.object.Position;
import kuraeyong.backend.object.StationInfoLineListElement;
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

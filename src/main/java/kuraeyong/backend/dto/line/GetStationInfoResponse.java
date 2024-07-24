package kuraeyong.backend.dto.line;

import kuraeyong.backend.object.Position;
import kuraeyong.backend.object.UpDownLineListElement;
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
    private List<UpDownLineListElement> upLineList;
    private List<UpDownLineListElement> downLineList;
    private Position pos;
    private List<String> facilityList;
}

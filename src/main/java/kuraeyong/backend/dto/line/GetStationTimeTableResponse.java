package kuraeyong.backend.dto.line;

import kuraeyong.backend.object.Position;
import kuraeyong.backend.object.StationInfoLineListElement;
import kuraeyong.backend.object.StationTimeTableLineListElement;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetStationTimeTableResponse {
    private List<StationTimeTableLineListElement> upLineList;
    private List<StationTimeTableLineListElement> downLineList;
}

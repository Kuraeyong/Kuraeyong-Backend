package kuraeyong.backend.dto.response.line;

import kuraeyong.backend.dto.element.StationTimeTableLineListElement;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetStationTimeTableResponse {
    private List<StationTimeTableLineListElement> upLineList;
    private List<StationTimeTableLineListElement> downLineList;
}

package kuraeyong.backend.dto.station;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetLineListResponse {
    private List<List<String>> list;
}

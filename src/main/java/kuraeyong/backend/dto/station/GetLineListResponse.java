package kuraeyong.backend.dto.station;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetLineListResponse {
    private List<String> list;  /* 해당 역이 속하는 노선명 리스트 */
}

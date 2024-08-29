package kuraeyong.backend.dto.element;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MinimumStationInfo {
    private String railOprIsttCd;
    private String lnCd;
    private String stinCd;
}

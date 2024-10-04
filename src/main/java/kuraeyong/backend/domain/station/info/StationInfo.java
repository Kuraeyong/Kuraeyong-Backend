package kuraeyong.backend.domain.station.info;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StationInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String railOprIsttCd;

    @Column
    private String lnCd;

    @Column
    private String stinCd;

    @Column
    private String stinNm;

    @Column
    private int upDownOrder;

    @Column
    private String branchInfo;
}

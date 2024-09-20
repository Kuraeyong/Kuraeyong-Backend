package kuraeyong.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StationTrfWeight {

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
    private String trfRailOprIsttCd;

    @Column
    private String trfLnCd;

    @Column
    private String trfStinCd;

    @Column
    private String trfStinNm;

    @Column
    private String trfType;

    @Column
    private int upUp;

    @Column
    private int upDown;

    @Column
    private int downUp;

    @Column
    private int downDown;
}

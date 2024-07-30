package kuraeyong.backend.dto;

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
public class StationTimeTableDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String lnCd;

    @Column
    private String orgStinCd;

    @Column
    private String dayCd;

    @Column
    private String arvTm;

    @Column
    private String dayNm;

    @Column
    private String dptTm;

    @Column
    private String stinCd;

    @Column
    private String trnNo;

    @Column
    private String tmnStinCd;

    @Column
    private String railOprIsttCd;
}

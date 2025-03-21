package kuraeyong.backend.domain.graph;

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
public class EdgeInfo {

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
    private double weight;

    @Column
    private int isTrfStin;

    @Column
    private int isJctStin;

    @Column
    private int isExpStin;

    @Column
    private int edgeType;
}

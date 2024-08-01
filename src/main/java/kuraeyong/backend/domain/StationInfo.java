package kuraeyong.backend.domain;

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
    private String railOprIsttNm;

    @Column
    private String lnCd;

    @Column
    private String lnNm;

    @Column
    private String stinNo;

    @Column
    private String stinCd;

    @Column
    private String stinNm;
}

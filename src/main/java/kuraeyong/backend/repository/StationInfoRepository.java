package kuraeyong.backend.repository;

import kuraeyong.backend.domain.station.info.StationInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationInfoRepository extends JpaRepository<StationInfo, Long> {
    List<StationInfo> findByStinNm(String stinNm);
}

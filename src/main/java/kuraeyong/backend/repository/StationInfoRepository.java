package kuraeyong.backend.repository;

import kuraeyong.backend.domain.StationInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationInfoRepository extends JpaRepository<StationInfo, Long> {
//    List<Station> findAllByName(String name);
    List<StationInfo> findByStinNm(String stinNm);
}

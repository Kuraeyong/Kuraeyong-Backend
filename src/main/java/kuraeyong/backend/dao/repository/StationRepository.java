package kuraeyong.backend.dao.repository;

import kuraeyong.backend.domain.StationInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<StationInfo, Long> {
//    List<Station> findAllByName(String name);

}

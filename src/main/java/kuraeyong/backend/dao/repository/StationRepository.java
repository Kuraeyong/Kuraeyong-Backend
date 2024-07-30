package kuraeyong.backend.dao.repository;

import kuraeyong.backend.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {
//    List<Station> findAllByName(String name);

}

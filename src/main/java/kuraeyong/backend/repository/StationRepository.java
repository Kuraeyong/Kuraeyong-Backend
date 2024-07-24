package kuraeyong.backend.repository;

import kuraeyong.backend.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StationRepository extends JpaRepository<Station, Long> {
    List<Station> findAllByName(String name);
}

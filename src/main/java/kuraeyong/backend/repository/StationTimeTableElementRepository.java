package kuraeyong.backend.repository;

import kuraeyong.backend.domain.station.time_table.StationTimeTableElement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationTimeTableElementRepository extends JpaRepository<StationTimeTableElement, Long> {
}

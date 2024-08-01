package kuraeyong.backend.dao.repository;

import kuraeyong.backend.domain.StationTimeTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationTimeTableRepository extends JpaRepository<StationTimeTable, Long> {
}

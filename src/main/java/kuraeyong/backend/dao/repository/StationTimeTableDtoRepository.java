package kuraeyong.backend.dao.repository;

import kuraeyong.backend.domain.Station;
import kuraeyong.backend.dto.StationTimeTableDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationTimeTableDtoRepository extends JpaRepository<StationTimeTableDto, Long> {
}

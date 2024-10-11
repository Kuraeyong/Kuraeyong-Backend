package kuraeyong.backend.repository;

import kuraeyong.backend.domain.station.congestion.StationCongestion;
import kuraeyong.backend.domain.station.trf_weight.StationTrfWeight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationCongestionRepository extends JpaRepository<StationCongestion, Long> {
}

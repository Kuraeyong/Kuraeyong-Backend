package kuraeyong.backend.repository;

import kuraeyong.backend.domain.station.trf_weight.StationTrfWeight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationTrfWeightRepository extends JpaRepository<StationTrfWeight, Long> {
}

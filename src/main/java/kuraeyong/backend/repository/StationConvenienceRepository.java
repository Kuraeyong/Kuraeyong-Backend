package kuraeyong.backend.repository;

import kuraeyong.backend.domain.station.convenience.StationConvenience;
import kuraeyong.backend.domain.station.trf_weight.StationTrfWeight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationConvenienceRepository extends JpaRepository<StationConvenience, Long> {
}

package kuraeyong.backend.repository;

import kuraeyong.backend.domain.EdgeInfo;
import kuraeyong.backend.domain.StationInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EdgeInfoRepository extends JpaRepository<EdgeInfo, Long> {
}

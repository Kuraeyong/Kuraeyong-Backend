package kuraeyong.backend.repository;

import kuraeyong.backend.domain.EdgeInfo;
import org.apache.commons.math3.geometry.spherical.twod.Edge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EdgeInfoRepository extends JpaRepository<EdgeInfo, Long> {
    List<EdgeInfo> findByIsTrfStinGreaterThan(int number);
}

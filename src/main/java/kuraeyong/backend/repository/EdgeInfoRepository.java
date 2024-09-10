package kuraeyong.backend.repository;

import kuraeyong.backend.domain.EdgeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EdgeInfoRepository extends JpaRepository<EdgeInfo, Long> {
    @Query("SELECT e FROM EdgeInfo e WHERE (e.isTrfStin > 0 OR e.isExpStin > 0) AND e.isExpEdge = 0")
    List<EdgeInfo> findGeneralEdgeInfo();
    List<EdgeInfo> findByIsExpEdgeGreaterThan(int number);
    List<EdgeInfo> findByRailOprIsttCdAndLnCdAndStinCd(String railOprIsttCd, String lnCd, String stinCd);
}

package kuraeyong.backend.repository;

import kuraeyong.backend.domain.graph.EdgeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EdgeInfoRepository extends JpaRepository<EdgeInfo, Long> {
    @Query("SELECT e FROM EdgeInfo e WHERE (e.isTrfStin > 0 OR e.isExpStin > 0) AND e.edgeType != 1")
    List<EdgeInfo> findNotExpEdgeInfo();

    List<EdgeInfo> findByEdgeTypeEquals(int number);

    List<EdgeInfo> findByRailOprIsttCdAndLnCdAndStinCd(String railOprIsttCd, String lnCd, String stinCd);
}
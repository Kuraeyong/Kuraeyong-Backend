package kuraeyong.backend.manager.station;

import kuraeyong.backend.domain.graph.EdgeInfo;
import kuraeyong.backend.repository.EdgeInfoRepository;
import kuraeyong.backend.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EdgeInfoManager implements StationDBInitializer {
    private final EdgeInfoRepository edgeInfoRepository;

    @Override
    public boolean initDB(List<List<String>> rowList) {
        List<EdgeInfo> edgeInfoList = Converter.toEdgeInfos(rowList);

        edgeInfoRepository.deleteAll();
        return edgeInfoList.size() == edgeInfoRepository.saveAll(edgeInfoList).size();
    }

    public List<EdgeInfo> findNotExpEdgeInfo() {
        return edgeInfoRepository.findNotExpEdgeInfo();
    }

    public List<EdgeInfo> findByEdgeTypeEquals(int number) {
        return edgeInfoRepository.findByEdgeTypeEquals(number);
    }

    public List<EdgeInfo> findByRailOprIsttCdAndLnCdAndStinCd(String railOprIsttCd, String lnCd, String stinCd) {
        return edgeInfoRepository.findByRailOprIsttCdAndLnCdAndStinCd(railOprIsttCd, lnCd, stinCd);
    }
}

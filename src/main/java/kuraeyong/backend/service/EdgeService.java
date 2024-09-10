package kuraeyong.backend.service;

import kuraeyong.backend.domain.EdgeInfo;
import kuraeyong.backend.repository.EdgeInfoRepository;
import kuraeyong.backend.util.FlatFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EdgeService {

    private final EdgeInfoRepository edgeInfoRepository;
    private final static String BASE_URL = "src/main/resources/xlsx/";

    public String createEdgeInfoDB() {
        String file = BASE_URL + "edge_info.xlsx";
        List<List<String>> rowList = FlatFileUtil.getDataListFromExcel(file);
        List<EdgeInfo> edgeInfoList = FlatFileUtil.toEdgeInfoList(rowList);

//        edgeInfoRepository.deleteAll();
        List<EdgeInfo> saveResult = edgeInfoRepository.saveAll(edgeInfoList);

        if (saveResult.size() == edgeInfoList.size()) {
            return "SUCCESS";
        }
        return "FAILED";
    }
}

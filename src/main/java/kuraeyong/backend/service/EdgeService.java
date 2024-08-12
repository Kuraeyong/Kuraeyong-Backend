package kuraeyong.backend.service;

import kuraeyong.backend.repository.EdgeInfoRepository;
import kuraeyong.backend.domain.EdgeInfo;
import kuraeyong.backend.util.Converter;
import kuraeyong.backend.util.FlatFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EdgeService {

    @Autowired
    private EdgeInfoRepository edgeInfoRepository;

    public String createEdgeInfoDB() {
        List<List<String>> rowList = FlatFileUtil.getDataListFromExcel("src/main/resources/xlsx/edge_info.xlsx");
        List<EdgeInfo> edgeInfoList = Converter.toEdgeInfoList(rowList);

//        edgeInfoRepository.deleteAll();
        List<EdgeInfo> saveResult = edgeInfoRepository.saveAll(edgeInfoList);

        if (saveResult.size() == edgeInfoList.size()) {
            return "SUCCESS";
        }
        return "FAILED";
    }
}

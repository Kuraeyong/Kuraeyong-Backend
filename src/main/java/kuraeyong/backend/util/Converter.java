package kuraeyong.backend.util;

import kuraeyong.backend.domain.EdgeInfo;
import kuraeyong.backend.domain.StationInfo;

import java.util.ArrayList;
import java.util.List;

public class Converter {
    public static List<StationInfo> toStationInfoList(List<List<String>> rowList) {
        List<StationInfo> stationInfoList = new ArrayList<>();

        for (List<String> row : rowList) {
            stationInfoList.add(StationInfo.builder()
                    .railOprIsttCd(row.get(0))
                    .railOprIsttNm(row.get(1))
                    .lnCd(row.get(2))
                    .lnNm(row.get(3))
                    .stinNo(row.get(4))
                    .stinCd(row.get(5))
                    .stinNm(row.get(6))
                    .build());
        }

        return stationInfoList;
    }

    public static List<EdgeInfo> toEdgeInfoList(List<List<String>> rowList) {
        List<EdgeInfo> edgeInfoList = new ArrayList<>();

        for (List<String> row : rowList) {
            edgeInfoList.add(EdgeInfo.builder()
                    .railOprIsttCd(row.get(0))
                    .lnCd(row.get(1))
                    .stinCd(row.get(2))
                    .stinNm(row.get(3))
                    .trfRailOprIsttCd(row.get(4))
                    .trfLnCd(row.get(5))
                    .trfStinCd(row.get(6))
                    .trfStinNm(row.get(7))
                    .weight(row.get(8))
                    .isTrfStin(row.get(9))
                    .build());
        }

        return edgeInfoList;
    }
}

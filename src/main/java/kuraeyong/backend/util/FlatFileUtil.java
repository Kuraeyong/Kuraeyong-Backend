package kuraeyong.backend.util;

import kuraeyong.backend.domain.EdgeInfo;
import kuraeyong.backend.domain.StationInfo;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FlatFileUtil {

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
                    .weight(Double.parseDouble(row.get(8)))
                    .isTrfStin(Integer.parseInt(row.get(9).split("\\.")[0]))
                    .isJctStin(Integer.parseInt(row.get(10).split("\\.")[0]))
                    .isExpStin(Integer.parseInt(row.get(11).split("\\.")[0]))
                    .isExpEdge(Integer.parseInt(row.get(12).split("\\.")[0]))
                    .build());
        }

        return edgeInfoList;
    }

    public static List<List<String>> getDataListFromExcel(String filePath) {
        List<List<String>> rowList = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);

            int rows = sheet.getPhysicalNumberOfRows();
            for (int rowIndex = 1; rowIndex < rows; rowIndex++) {
                List<String> rowElementList = new ArrayList<>();
                XSSFRow row = sheet.getRow(rowIndex);
                if (row != null) {
                    int cells = row.getPhysicalNumberOfCells();
                    for (int columnIndex = 0; columnIndex < cells; columnIndex++) {
                        XSSFCell cell = row.getCell(columnIndex);
                        String value = switch (cell.getCellType()) { // 각 셀에 담겨있는 데이터의 타입을 체크하고 해당 타입에 맞게 가져온다.
                            case NUMERIC -> cell.getNumericCellValue() + "";
//                            case NUMERIC -> cell.getStringCellValue();
                            case STRING -> cell.getStringCellValue();
                            case BLANK -> cell.getBooleanCellValue() + "";
                            case ERROR -> cell.getErrorCellValue() + "";
                            default -> "";
                        };
                        rowElementList.add(value);
                    }
                }
                rowList.add(rowElementList);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return rowList;
    }
}

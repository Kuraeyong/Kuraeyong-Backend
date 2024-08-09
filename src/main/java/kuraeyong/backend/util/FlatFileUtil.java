package kuraeyong.backend.util;

import kuraeyong.backend.common.response.BaseResponse;
import kuraeyong.backend.dao.repository.StationInfoRepository;
import kuraeyong.backend.domain.StationInfo;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FlatFileUtil {

    public static List<List<String>> getDataListFromExcel(String filePath) {
        List<List<String>> rowList = new ArrayList<>();
//        List<String> rowElementList = new ArrayList<>();
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
//                rowElementList.clear();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return rowList;
    }

    public static String ListTo(List<List<String>> rowList, Object object) {
        return "ok";
    }

    @Autowired
    private StationInfoRepository stationInfoRepository;

    public String initStationDB() {
        List<StationInfo> stationInfoList = FlatFileUtil.getStationListFromExcel("src/main/resources/xlsx/station_code_info.xlsx");
        stationInfoRepository.deleteAll();
        List<StationInfo> saveResult = stationInfoRepository.saveAll(stationInfoList);
        if (saveResult.size() == stationInfoList.size()) {
            return "SUCCESS";
        }
        return "FAILED";
    }

    public static List<StationInfo> getStationListFromExcel(String filePath) {
        List<StationInfo> stationInfoList = new ArrayList<>();
        List<String> stationElementList = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);

            int rows = sheet.getPhysicalNumberOfRows();
            for (int rowIndex = 1; rowIndex < rows; rowIndex++) {
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
                        stationElementList.add(value);
                    }
                }

                StationInfo stationInfo = StationInfo.builder()
                        .railOprIsttCd(stationElementList.get(0))
                        .railOprIsttNm(stationElementList.get(1))
                        .lnCd(stationElementList.get(2))
                        .lnNm(stationElementList.get(3))
                        .stinNo(stationElementList.get(4))
                        .stinCd(stationElementList.get(5))
                        .stinNm(stationElementList.get(6))
                        .build();
                stationInfoList.add(stationInfo);
                stationElementList.clear();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stationInfoList;
    }
}

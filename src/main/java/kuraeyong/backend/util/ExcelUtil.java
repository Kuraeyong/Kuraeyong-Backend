package kuraeyong.backend.util;

import kuraeyong.backend.domain.Station;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {

//    public static void printExcel(String filePath) {
//        try {
//            FileInputStream fis = new FileInputStream(filePath);
//            XSSFWorkbook workbook = new XSSFWorkbook(fis);
//            XSSFSheet sheet = workbook.getSheetAt(0);
//            int rows = sheet.getPhysicalNumberOfRows();
//            System.out.printf("rows: %s\n", rows);
//            for (int rowIndex = 1; rowIndex < rows; rowIndex++) {
//                XSSFRow row = sheet.getRow(rowIndex);
//                if (row != null) {
//                    int cells = row.getPhysicalNumberOfCells();
//                    System.out.printf("cells: %s\n", cells);
//                    for (int columnIndex = 0; columnIndex < cells; columnIndex++) {
//                        XSSFCell cell = row.getCell(columnIndex);
//                        String value = switch (cell.getCellType()) { // 각 셀에 담겨있는 데이터의 타입을 체크하고 해당 타입에 맞게 가져온다.
//                            case NUMERIC -> cell.getNumericCellValue() + "";
//                            case STRING -> cell.getStringCellValue();
//                            case BLANK -> cell.getBooleanCellValue() + "";
//                            case ERROR -> cell.getErrorCellValue() + "";
//                            default -> "";
//                        };
//                        System.out.println(value);
//                    }
//                }
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public static List<Station> getStationListFromExcel(String filePath) {
        List<Station> stationList = new ArrayList<>();
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

                Station station = Station.builder()
                        .railOprIsttCd(stationElementList.get(0))
                        .railOprIsttNm(stationElementList.get(1))
                        .lnCd(stationElementList.get(2))
                        .lnNm(stationElementList.get(3))
                        .stinNo(stationElementList.get(4))
                        .stinCd(stationElementList.get(5))
                        .stinNm(stationElementList.get(6))
                        .build();
                stationList.add(station);
                stationElementList.clear();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stationList;
    }
}

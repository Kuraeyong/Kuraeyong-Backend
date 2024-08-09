package kuraeyong.backend.controller;

import kuraeyong.backend.util.FlatFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    @GetMapping("/excel")
    public String getDataListFromExcelTest() {
        log.info("[TestController.getDataListFromExcelTest]");

        List<List<String>> rowList = FlatFileUtil.getDataListFromExcel("src/main/resources/xlsx/station_code_info.xlsx");
        for (List<String> row : rowList) {
            for (String cell : row) {
                System.out.printf("%s\t", cell);
            }
            System.out.println();
        }
//        System.out.println(rowList.size());
        return "ok";
    }

    @GetMapping("")
    public String test() {
//        FlatFileUtil.ListToEntityList(FlatFileUtil.getDataListFromExcel("src/main/resources/xlsx/station_code_info.xlsx"), EntityType.STATION_INFO);
        return "ok";
    }
}

package kuraeyong.backend.service;

import kuraeyong.backend.dao.StationDao;
import kuraeyong.backend.domain.StationInfo;
import kuraeyong.backend.util.OpenApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationService {

    private final StationDao stationDao;

    public String createStationDB() {
        // TODO: Station DB 생성 및 초기화
        return stationDao.initStationDB();
    }

    // 반환값 수정 필요
    public void saveApiResultToCsv() {
        // TODO: API 결과 CSV 파일에 저장
        List<StationInfo> stationInfoList = stationDao.getStationList();
        String format = "json";
        String dayCd = "7";
        int stationCount = 0, lineCount = 0, logCount = 0;

        // 파일명 및 로그파일명 지정 (덮어쓰기로 진행)
        String filePath = "src/main/resources/station_time_table_saturday.csv";
        String logFilePath = "src/main/resources/station_time_table_saturday_log.csv";
        File file, logFile;
        BufferedWriter bw, logBw;
        String NEWLINE = System.lineSeparator();    // 개행

        try {
            file = new File(filePath);
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            bw.write("\uFEFF");
            bw.write("LN_CD," +
                    "ORG_STIN_CD," +
                    "DAY_CD," +
                    "ARV_TM," +
                    "DAY_NM," +
                    "DPT_TM," +
                    "STIN_CD," +
                    "TRN_NO," +
                    "TMN_STIN_CD," +
                    "RAIL_OPR_ISTT_CD");
            bw.write(NEWLINE);

            logFile = new File(logFilePath);
            logBw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile), StandardCharsets.UTF_8));
            logBw.write("\uFEFF");
            logBw.write("LINE_NO," +
                    "RAIL_OPR_ISTT_CD," +
                    "LN_CD," +
                    "STIN_CD," +
                    "DAY_CD");
            logBw.write(NEWLINE);

            for (StationInfo stationInfo : stationInfoList) {
                System.out.println("CurrentStationCount: " + ++stationCount);
                String railOprIsttCd = stationInfo.getRailOprIsttCd();
                String lnCd = stationInfo.getLnCd();
                String stinCd = stationInfo.getStinCd();
                String queryString = "&format=" + format +
                        "&railOprIsttCd=" + railOprIsttCd +
                        "&lnCd=" + lnCd +
                        "&stinCd=" + stinCd +
                        "&dayCd=" + dayCd;
                JSONArray parseBody = getParseBody(queryString);
                if (parseBody == null) {
                    logBw.write(++logCount + "," +
                            railOprIsttCd + "," +
                            lnCd + "," +
                            stinCd + "," +
                            dayCd);
                    logBw.write(NEWLINE);
                    continue;
                }

                // csv 파일에 저장
                for (Object parseLine : parseBody) {
                    lineCount++;
                    JSONObject line = (JSONObject) parseLine;
                    bw.write(line.get("lnCd") + "," +
                            line.get("orgStinCd") + "," +
                            line.get("dayCd") + "," +
                            line.get("arvTm") + "," +
                            line.get("dayNm") + "," +
                            line.get("dptTm") + "," +
                            line.get("stinCd") + "," +
                            line.get("trnNo") + "," +
                            line.get("tmnStinCd") + "," +
                            line.get("railOprIsttCd"));
                    bw.write(NEWLINE);
                }
            }
            bw.flush();
            bw.close();
            logBw.flush();
            logBw.close();
            System.out.println("StationCount: " + stationCount);
            System.out.println("lineCount: " + lineCount);
            System.out.println("logCount: " + logCount);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadCsv() {
        String filePath = "src/main/resources/station_time_table_holiday.csv";
        File file;
        BufferedReader br;

        file = new File(filePath);
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static JSONArray getParseBody(String queryString) throws IOException, ParseException {
        String urlStr = OpenApiUtil.getKricOpenApiURL("convenientInfo", "stationTimetable", queryString);

        URL url = new URL(urlStr);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));

        // json 파싱 객체 생성
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(br.readLine());
//                System.out.println("jsonObject: " + jsonObject);

        // body 받아오기
        return (JSONArray) jsonObject.get("body");
    }

//    public GetListResponse getLineNameListByStationName(String stationName) {
//        log.info("[StationService.getLineNameListByStationName]");
//
//        // TODO: 해당 역이 속한 노선명 리스트 조회
//        return stationDao.getLineNameListByStationName(stationName);
//    }
}

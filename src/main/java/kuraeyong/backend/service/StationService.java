package kuraeyong.backend.service;

import kuraeyong.backend.domain.StationInfo;
import kuraeyong.backend.domain.StationTrfWeight;
import kuraeyong.backend.domain.MinimumStationInfo;
import kuraeyong.backend.repository.StationInfoRepository;
import kuraeyong.backend.repository.StationTrfWeightRepository;
import kuraeyong.backend.util.FlatFileUtil;
import kuraeyong.backend.util.OpenApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationService {

    private final StationInfoRepository stationInfoRepository;
    private final StationTrfWeightRepository stationTrfWeightRepository;
    private final static String BASE_URL = "src/main/resources/xlsx/";
    private static String csvFilePath, logFilePath;

    @Value("${csv-file-path}")
    public void setCsvFilePath(String path) {
        csvFilePath = path;
    }

    @Value("${log-file-path}")
    public void setLogFilePath(String path) {
        logFilePath = path;
    }

    /**
     *  StationInfo DB 생성 및 초기화
     */
    public String createStationInfoDB() {
        String filePath = BASE_URL + "station_info.xlsx";
        List<List<String>> rowList = FlatFileUtil.getDataListFromExcel(filePath);
        List<StationInfo> stationInfoList = FlatFileUtil.toStationInfoList(rowList);

        stationInfoRepository.deleteAll();
        List<StationInfo> saveResult = stationInfoRepository.saveAll(stationInfoList);

        if (saveResult.size() == stationInfoList.size()) {
            return "SUCCESS";
        }
        return "FAILED";
    }

    public String createStationTrfWeightDB() {
        String filePath = BASE_URL + "station_trf_weight.xlsx";
        List<List<String>> rowList = FlatFileUtil.getDataListFromExcel(filePath);
        List<StationTrfWeight> stationTrfWeightList = FlatFileUtil.toStationTrfWeightList(rowList);

        stationTrfWeightRepository.deleteAll();
        List<StationTrfWeight> saveResult = stationTrfWeightRepository.saveAll(stationTrfWeightList);

        if (saveResult.size() == stationTrfWeightList.size()) {
            return "SUCCESS";
        }
        return "FAILED";
    }

    /**
     * 역사 시간표와 관련한 API 응답 결과를 station_time_table_[csvFilePath]에 저장
     */
    public void saveApiResultToCsv() {
        List<StationInfo> stationInfoList = stationInfoRepository.findAll();
        String format = "json";
        String dayNm = csvFilePath.split("[_.]")[3];
        String dayCd = switch (dayNm) {
            case "saturday" -> "7";
            case "weekday" -> "8";
            case "holiday" -> "9";
            default -> null;
        };
        int stationCount = 0, lineCount = 0, logCount = 0;

        File file, logFile;
        BufferedWriter bw, logBw;
        String NEWLINE = System.lineSeparator();    // 개행

        try {
            file = new File(csvFilePath);
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

                // TODO. CSV 파일에 저장
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
        // FIXME: 반환값 수정 필요
    }

    /**
     * CSV 파일이 정상적으로 로드되는지 테스트
     */
    public void loadCsv() {
        File file;
        BufferedReader br;

        file = new File(csvFilePath);
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

    /**
     *  JSON 데이터 파싱
     */
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

    /**
     * 역명으로 고유역 조회
     */
    public MinimumStationInfo getStationByName(String stinNm) {
        List<StationInfo> stationInfoList = stationInfoRepository.findByStinNm(stinNm);
        if (stationInfoList == null) {
            return null;
        }

        StationInfo row = stationInfoList.get(0);
        return MinimumStationInfo.build(row.getRailOprIsttCd(), row.getLnCd(), row.getStinCd());
    }
}

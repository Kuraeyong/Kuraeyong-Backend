package kuraeyong.backend.service;

import kuraeyong.backend.common.exception.DomainInitializationException;
import kuraeyong.backend.common.exception.ErrorMessage;
import kuraeyong.backend.common.response.BaseResponse;
import kuraeyong.backend.common.response.ResponseStatus;
import kuraeyong.backend.domain.constant.FileType;
import kuraeyong.backend.domain.station.info.MinimumStationInfo;
import kuraeyong.backend.domain.station.info.StationInfo;
import kuraeyong.backend.manager.station.EdgeInfoManager;
import kuraeyong.backend.manager.station.StationCongestionManager;
import kuraeyong.backend.manager.station.StationConvenienceManager;
import kuraeyong.backend.manager.station.StationDBInitializer;
import kuraeyong.backend.manager.station.StationInfoManager;
import kuraeyong.backend.manager.station.StationTrfWeightManager;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationService {

    private final StationInfoManager stationInfoManager;
    private final StationTrfWeightManager stationTrfWeightManager;
    private final StationCongestionManager stationCongestionManager;
    private final StationConvenienceManager stationConvenienceManager;
    private final EdgeInfoManager edgeInfoManager;
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
     * 역 이름을 통해 역을 조회
     *
     * @param stinNm 역 이름
     * @return 해당 이름을 가진 역
     */
    public MinimumStationInfo getStationByName(String stinNm) {
        return stationInfoManager.getStationByName(stinNm);
    }

    /**
     * 파일 종류에 맞는 데이터베이스를 초기화
     *
     * @param fileType 파일 종류
     * @return 초기화 성공 여부
     */
    public ResponseStatus initDB(FileType fileType) {
        StationDBInitializer manager = getManager(fileType);
        String filePath = BASE_URL + fileType.getFileName();
        List<List<String>> rowList = FlatFileUtil.getDataListFromExcel(filePath);

        if (manager.initDB(rowList)) {
            return new BaseResponse<>();
        }
        throw new DomainInitializationException(ErrorMessage.DOMAIN_INITIALIZATION_FAILED);
    }

    /**
     * 파일 종류에 맞는 매니저를 반환
     *
     * @param fileType 파일 종류
     * @return 파일 종류에 맞는 매니저
     */
    private StationDBInitializer getManager(FileType fileType) {
        if (fileType == FileType.STATION_INFO) {
            return stationInfoManager;
        }
        if (fileType == FileType.STATION_TRF_WEIGHT) {
            return stationTrfWeightManager;
        }
        if (fileType == FileType.STATION_CONGESTION) {
            return stationCongestionManager;
        }
        if (fileType == FileType.STATION_CONVENIENCE) {
            return stationConvenienceManager;
        }
        return edgeInfoManager;
    }

    /**
     * 역사 시간표와 관련한 API 응답 결과를 station_time_table_[csvFilePath]에 저장
     */
    public String saveStationTimeTableAPIResultToCsv() {
        // 쿼리스트링 정적 요소 사전 초기화
        String format = "json";
        String dayNm = csvFilePath.split("[_.]")[3];
        String dayCd = switch (dayNm) {
            case "saturday" -> "7";
            case "weekday" -> "8";
            case "holiday" -> "9";
            default -> null;
        };

        File file, logFile;
        BufferedWriter bw, logBw;
        String NEWLINE = System.lineSeparator();    // 개행
        int stationCount = 0, lineCount = 0, logCount = 0;

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

            for (StationInfo stationInfo : stationInfoManager.findAll()) {
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

                // 결과가 없는 경우, 로그 기록
                if (parseBody == null) {
                    logBw.write(++logCount + "," +
                            railOprIsttCd + "," +
                            lnCd + "," +
                            stinCd + "," +
                            dayCd);
                    logBw.write(NEWLINE);
                    continue;
                }

                // CSV 파일에 저장
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
        return "SUCCESS";
    }

    /**
     * CSV 파일 콘솔에 로드
     */
    public String loadCsv() {
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
        return "SUCCESS";
    }

    /**
     * JSON 데이터 파싱
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
}

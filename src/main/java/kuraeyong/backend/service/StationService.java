package kuraeyong.backend.service;

import kuraeyong.backend.domain.constant.FileType;
import kuraeyong.backend.domain.graph.EdgeInfo;
import kuraeyong.backend.domain.station.congestion.StationCongestion;
import kuraeyong.backend.domain.station.convenience.StationConvenience;
import kuraeyong.backend.domain.station.info.MinimumStationInfo;
import kuraeyong.backend.domain.station.info.StationInfo;
import kuraeyong.backend.domain.station.trf_weight.StationTrfWeight;
import kuraeyong.backend.repository.*;
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
    private final StationCongestionRepository stationCongestionRepository;
    private final StationConvenienceRepository stationConvenienceRepository;
    private final EdgeInfoRepository edgeInfoRepository;
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

    public String initDB(FileType fileType) {
        String filePath = BASE_URL + fileType.getFileName();
        List<List<String>> rowList = FlatFileUtil.getDataListFromExcel(filePath);
        boolean isFinished;

        if (fileType == FileType.STATION_INFO) {
            isFinished = initStationInfoDB(rowList);
        } else if (fileType == FileType.STATION_TRF_WEIGHT) {
            isFinished = initStationTrfWeightDB(rowList);
        } else if (fileType == FileType.STATION_CONGESTION) {
            isFinished = initStationCongestionDB(rowList);
        } else if (fileType == FileType.STATION_CONVENIENCE) {
            isFinished = initStationConvenienceDB(rowList);
        } else {
            isFinished = initEdgeInfoDB(rowList);
        }
        
        if (isFinished) {
            return "SUCCESS";
        }
        return "FAILED";
    }
    
    private boolean initStationInfoDB(List<List<String>> rowList) {
        List<StationInfo> stationInfoList = FlatFileUtil.toStationInfoList(rowList);
        
        stationInfoRepository.deleteAll();
        return stationInfoList.size() == stationInfoRepository.saveAll(stationInfoList).size();
    }

    private boolean initStationTrfWeightDB(List<List<String>> rowList) {
        List<StationTrfWeight> stationTrfWeightList = FlatFileUtil.toStationTrfWeightList(rowList);

        stationTrfWeightRepository.deleteAll();
        return stationTrfWeightList.size() == stationTrfWeightRepository.saveAll(stationTrfWeightList).size();
    }

    private boolean initStationCongestionDB(List<List<String>> rowList) {
        List<StationCongestion> stationCongestionList = FlatFileUtil.toStationCongestionList(rowList);

        stationCongestionRepository.deleteAll();
        return stationCongestionList.size() == stationCongestionRepository.saveAll(stationCongestionList).size();
    }

    private boolean initStationConvenienceDB(List<List<String>> rowList) {
        List<StationConvenience> stationConvenienceList = FlatFileUtil.toStationConvenienceList(rowList);

        stationConvenienceRepository.deleteAll();
        return stationConvenienceList.size() == stationConvenienceRepository.saveAll(stationConvenienceList).size();
    }

    private boolean initEdgeInfoDB(List<List<String>> rowList) {
        List<EdgeInfo> edgeInfoList = FlatFileUtil.toEdgeInfoList(rowList);

        edgeInfoRepository.deleteAll();
        return edgeInfoList.size() == edgeInfoRepository.saveAll(edgeInfoList).size();
    }

    /**
     * 역사 시간표와 관련한 API 응답 결과를 station_time_table_[csvFilePath]에 저장
     */
    public String saveStationTimeTableAPIResultToCsv() {
        // TODO. 쿼리스트링 정적 요소 사전 초기화
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

            for (StationInfo stationInfo : stationInfoRepository.findAll()) {
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

                // TODO. 결과가 없는 경우, 로그 기록
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
        if (stationInfoList.isEmpty()) {
            return null;
        }

        StationInfo row = stationInfoList.get(0);
        return MinimumStationInfo.build(row.getRailOprIsttCd(), row.getLnCd(), row.getStinCd());
    }
}

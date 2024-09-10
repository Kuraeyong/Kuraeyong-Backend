package kuraeyong.backend.service;

import kuraeyong.backend.domain.MetroNodeWithEdge;
import kuraeyong.backend.domain.MetroPath;
import kuraeyong.backend.domain.StationInfo;
import kuraeyong.backend.domain.StationTimeTableMap;
import kuraeyong.backend.dto.MinimumStationInfo;
import kuraeyong.backend.dto.MinimumStationInfoWithDateType;
import kuraeyong.backend.dto.MoveInfo;
import kuraeyong.backend.repository.StationInfoRepository;
import kuraeyong.backend.util.DateUtil;
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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationService {

    private final StationInfoRepository stationInfoRepository;
    private final StationTimeTableMap stationTimeTableMap;
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
        List<List<String>> rowList = FlatFileUtil.getDataListFromExcel("src/main/resources/xlsx/station_code_info.xlsx");
        List<StationInfo> stationInfoList = FlatFileUtil.toStationInfoList(rowList);

        stationInfoRepository.deleteAll();
        List<StationInfo> saveResult = stationInfoRepository.saveAll(stationInfoList);

        if (saveResult.size() == stationInfoList.size()) {
            return "SUCCESS";
        }
        return "FAILED";
    }

    /**
     * API 결과 CSV 파일에 저장
     */
    public void saveApiResultToCsv() {
        List<StationInfo> stationInfoList = stationInfoRepository.findAll();
        String format = "json";
        String dayCd = "7";
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
        // FIXME: 반환값 수정 필요
    }

    /**
     * CSV 파일 로드
     */
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

        StationInfo stationInfo = stationInfoList.get(0);
        return MinimumStationInfo.builder()
                .railOprIsttCd(stationInfo.getRailOprIsttCd())
                .lnCd(stationInfo.getLnCd())
                .stinCd(stationInfo.getStinCd())
                .build();
    }

    /**
     *
     * @param compressedPath    e.g.) (K4, 행신, 0.0) (K4, 홍대입구, 2.5) (2, 홍대입구, 6.5) (2, 성수, 5.5) (2, 용답, 3.5)
     * @param dateType  날짜 종류 (평일 | 토요일 | 공휴일)
     * @param hour  사용자의 해당 역 도착 시간 (시간)
     * @param min   사용자의 해당 역 도착 시간 (분)
     * @return  이동 정보 리스트
     */
    public List<MoveInfo> getMoveInfoList(MetroPath compressedPath, String dateType, int hour, int min) {
        List<MoveInfo> moveInfoList = new ArrayList<>();

        String currTime = DateUtil.getCurrTime(hour, min);
        for (int i = 0; i < compressedPath.size() - 1; i++) {
            MetroNodeWithEdge curr = compressedPath.get(i);
            MetroNodeWithEdge next = compressedPath.get(i + 1);

            MoveInfo moveInfo = stationTimeTableMap.getMoveInfo(curr, next, dateType, currTime);
            currTime = moveInfo.getArvTm();
            moveInfoList.add(moveInfo);
        }

        return moveInfoList;
    }

    public double getAvgWaitingTime(MinimumStationInfoWithDateType stin) {
        return stationTimeTableMap.getAvgWaitingTime(stin);
    }
}

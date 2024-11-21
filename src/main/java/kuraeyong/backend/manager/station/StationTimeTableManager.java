package kuraeyong.backend.manager.station;

import kuraeyong.backend.common.exception.ErrorMessage;
import kuraeyong.backend.domain.station.info.StationInfo;
import kuraeyong.backend.util.OpenApiUtil;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StationTimeTableManager {
    private final static String NEW_LINE = System.lineSeparator();
    private final static String DELIMITER = ",";
    private static String csvFilePath;
    private static String logFilePath;

    @Value("${csv-file-path}")
    public void setCsvFilePath(String path) {
        csvFilePath = path;
    }

    @Value("${log-file-path}")
    public void setLogFilePath(String path) {
        logFilePath = path;
    }

    /**
     * 역사 시간표 API 응답 결과를 station_time_table_{csvFilePath}에 저장
     *
     * @param stationInfos 역 목록
     */
    public void saveApiResult2Csv(List<StationInfo> stationInfos) {
        // 쿼리스트링 정적 요소 사전 초기화
        String format = "json";
        String dayNm = csvFilePath.split("[_.]")[3];
        String dayCd = switch (dayNm) {
            case "saturday" -> "7";
            case "weekday" -> "8";
            case "holiday" -> "9";
            default -> throw new IllegalArgumentException(ErrorMessage.INVALID_DATE_TYPE.get());
        };
        try {
            BufferedWriter bw = initBufferedWriterWithHeader(csvFilePath, "LN_CD," +
                    "ORG_STIN_CD," +
                    "DAY_CD," +
                    "ARV_TM," +
                    "DAY_NM," +
                    "DPT_TM," +
                    "STIN_CD," +
                    "TRN_NO," +
                    "TMN_STIN_CD," +
                    "RAIL_OPR_ISTT_CD");
            BufferedWriter logBw = initBufferedWriterWithHeader(logFilePath, "RAIL_OPR_ISTT_CD," +
                    "LN_CD," +
                    "STIN_CD," +
                    "DAY_CD");
            for (int idx = 0; idx < stationInfos.size(); idx++) {
                System.out.println(idx);    // 현재 작업 수행을 완료한 역 개수
                StationInfo stationInfo = stationInfos.get(idx);
                String queryString = "&format=" + format +
                        "&railOprIsttCd=" + stationInfo.getRailOprIsttCd() +
                        "&lnCd=" + stationInfo.getLnCd() +
                        "&stinCd=" + stationInfo.getStinCd() +
                        "&dayCd=" + dayCd;
                JSONArray parseBody = getParseBody(queryString);
                if (saveParseBody2BufferedWriter(parseBody, bw)) {
                    continue;
                }
                logBw.write(stationInfo.getRailOprIsttCd() + DELIMITER +
                        stationInfo.getLnCd() + DELIMITER +
                        stationInfo.getStinCd() + DELIMITER +
                        dayCd + NEW_LINE);
            }
            closeAfterFlush(bw);
            closeAfterFlush(logBw);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 파일 작성에 필요한 BufferedWriter를 초기화하고 Header를 설정
     *
     * @param filePath 작성할 파일 경로
     * @param header   해당 파일의 헤더
     * @return Header가 설정된 BufferedWriter
     * @throws IOException 입출력 예외
     */
    private BufferedWriter initBufferedWriterWithHeader(String filePath, String header) throws IOException {
        File file = new File(filePath);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        bw.write("\uFEFF");
        bw.write(header);
        bw.write(NEW_LINE);
        return bw;
    }

    /**
     * JSON 데이터를 파싱한 결과를 반환
     *
     * @param queryString 쿼리스트링
     * @return JSON 데이터를 파싱한 결과 (Body)
     * @throws IOException    입출력 예외
     * @throws ParseException 파싱 예외
     */
    private static JSONArray getParseBody(String queryString) throws IOException, ParseException {
        URL url = new URL(OpenApiUtil.getKricOpenApiURL("convenientInfo", "stationTimetable", queryString));
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));

        // json 파싱 객체 생성
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(br.readLine());
//        System.out.println("jsonObject: " + jsonObject);

        // body 받아오기
        return (JSONArray) jsonObject.get("body");
    }

    /**
     * JSON 데이터 파싱한 결과를, 작성할 파일을 가리키는 BufferdWriter에 저장
     *
     * @param parseBody JSON 데이터를 파싱한 결과
     * @param bw        작성할 파일을 가리키는 BufferdWriter
     * @return 작성 성공 여부
     * @throws IOException 입출력 예외
     */
    private boolean saveParseBody2BufferedWriter(JSONArray parseBody, BufferedWriter bw) throws IOException {
        if (parseBody == null) {
            return false;
        }
        for (Object parseLine : parseBody) {
            JSONObject line = (JSONObject) parseLine;
            bw.write(line.get("lnCd") + DELIMITER +
                    line.get("orgStinCd") + DELIMITER +
                    line.get("dayCd") + DELIMITER +
                    line.get("arvTm") + DELIMITER +
                    line.get("dayNm") + DELIMITER +
                    line.get("dptTm") + DELIMITER +
                    line.get("stinCd") + DELIMITER +
                    line.get("trnNo") + DELIMITER +
                    line.get("tmnStinCd") + DELIMITER +
                    line.get("railOprIsttCd") + NEW_LINE);
        }
        return true;
    }

    private void closeAfterFlush(BufferedWriter bw) throws IOException {
        bw.flush();
        bw.close();
    }
}

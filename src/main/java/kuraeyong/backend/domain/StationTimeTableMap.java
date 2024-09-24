package kuraeyong.backend.domain;

import kuraeyong.backend.dto.MinimumStationInfo;
import kuraeyong.backend.dto.MinimumStationInfoWithDateType;
import kuraeyong.backend.repository.StationTimeTableElementRepository;
import kuraeyong.backend.util.DateUtil;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class StationTimeTableMap {
    private final HashMap<MinimumStationInfoWithDateType, StationTimeTable> map;

    public StationTimeTableMap(StationTimeTableElementRepository stationTimeTableElementRepository) {
        map = new HashMap<>();

        // TODO. 열차 정보 초기화
        for (StationTimeTableElement train : stationTimeTableElementRepository.findAll()) {
            if (train.getDptTm().equals("null") && train.getArvTm().equals("null")) {
                continue;
            }
            MinimumStationInfo MSI = MinimumStationInfo.build(train.getRailOprIsttCd(), train.getLnCd(), train.getStinCd());
            MinimumStationInfoWithDateType key = new MinimumStationInfoWithDateType(MSI, train.getDayNm());

            if (!map.containsKey(key)) {
                if (MSI.isSeongsu()) {
                    map.put(key, new SeongsuTimeTable());
                    continue;
                }
                map.put(key, new StationTimeTable());
            }
            map.get(key).add(train);
        }

        // TODO. 출발 시간을 기준으로 정렬
        Set<MinimumStationInfoWithDateType> keySet = map.keySet();
        for (MinimumStationInfoWithDateType key : keySet) {
            map.get(key).sort();
        }

        // TODO. 성수 처리
        MinimumStationInfo MSI = MinimumStationInfo.build("S1", "2", "211");
        List<String> dayNmList = new ArrayList<>(Arrays.asList("평일", "휴일"));

        for (String dayNm : dayNmList) {
            MinimumStationInfoWithDateType key = new MinimumStationInfoWithDateType(MSI, dayNm);
            SeongsuTimeTable seongsuTimeTable = (SeongsuTimeTable) map.get(key);
            HashMap<String, Integer> sameTrainMap = switch (dayNm) {
                case "평일" -> seongsuTimeTable.getWeekdaySameTrainMap();
                case "휴일" -> seongsuTimeTable.getHolidaySameTrainMap();
                default -> null;
            };
            assert sameTrainMap != null;

            // 성수에 도착하는 열차를 내선/외선, 기점/종점을 이용하여 총 4종류로 구분
            StationTimeTable inOrgTimeTable = new StationTimeTable();     // 내선기점
            StationTimeTable outOrgTimeTable = new StationTimeTable();    // 외선기점
            StationTimeTable inTmnTimeTable = new StationTimeTable();     // 내선종점
            StationTimeTable outTmnTimeTable = new StationTimeTable();    // 외선종점
            for (StationTimeTableElement train : seongsuTimeTable.getList()) {
                String trnNo = train.getTrnNo();
                boolean isOdd = trnNo.charAt(trnNo.length() - 1) % 2 == 1;

                if (trnNo.charAt(0) == '1') {    // 지선이면
                    continue;
                }
                if (isOdd) {    // 외선이면
                    if (train.getArvTm().equals("null")) {  // 기점이면
                        outOrgTimeTable.add(train);
                        continue;
                    }
                    outTmnTimeTable.add(train);
                    continue;
                }
                if (train.getArvTm().equals("null")) {  // 기점이면
                    inOrgTimeTable.add(train);
                    continue;
                }
                inTmnTimeTable.add(train);
            }

            int groupNum = initSameTrainMap(sameTrainMap, outTmnTimeTable, outOrgTimeTable, 0);    // 외선
            initSameTrainMap(sameTrainMap, inTmnTimeTable, inOrgTimeTable, groupNum);  // 내선
        }
    }

    /**
     * 성수가 종점인 열차의 도착시간(tmnTrainArvTm)과 성수가 기점인 가장 빠른 열차(orgFastestTrain)의 출발시간의 차가 2분 이내라면 동일한 열차로 간주
     */
    private static int initSameTrainMap(HashMap<String, Integer> sameTrainMap, StationTimeTable outTmnTimeTable, StationTimeTable outOrgTimeTable, int groupNum) {
        for (StationTimeTableElement tmnTrain : outTmnTimeTable.getList()) {
            String tmnTrainArvTm = tmnTrain.getArvTm();
            String tmnTrainTrnNo = tmnTrain.getTrnNo();
            if (!sameTrainMap.containsKey(tmnTrainTrnNo)) {
                sameTrainMap.put(tmnTrainTrnNo, groupNum++);
            }
            List<StationTimeTableElement> orgTrainList = outOrgTimeTable.findByDptTmGreaterThanEqual(tmnTrain.getArvTm());
            if (orgTrainList == null) {
                break;
            }
            StationTimeTableElement orgFastestTrain = orgTrainList.get(0);
            if (!DateUtil.isWithinNMinutes(tmnTrainArvTm, orgFastestTrain.getDptTm(), 2)) {
                continue;
            }
            sameTrainMap.put(orgFastestTrain.getTrnNo(), sameTrainMap.get(tmnTrainTrnNo));
        }
        return groupNum;
    }

    public StationTimeTable get(MinimumStationInfoWithDateType key) {
        return map.get(key);
    }

    /**
     * @param stin 고유한 역 정보
     * @return 특정역에서의 평균 배차시간을 반환
     */
    public double getAvgWaitingTime(MinimumStationInfoWithDateType stin) {
        if (!map.containsKey(stin)) {
            return -1;
        }
        StationTimeTable trainList = map.get(stin);
        StationTimeTableElement firstTrain = trainList.get(0);
        StationTimeTableElement lastTrain = trainList.get(trainList.size() - 1);
        int firstTrainArvTm = DateUtil.getTimeForCompare(firstTrain.getArvTm(), firstTrain.getDptTm());
        int lastTrainArvTm = DateUtil.getTimeForCompare(lastTrain.getArvTm(), lastTrain.getDptTm());
        int totalDuration = DateUtil.timeToMinute(lastTrainArvTm - firstTrainArvTm);
        double avgWaitingTime = (double) totalDuration / (trainList.size() - 1);

        return Math.round(avgWaitingTime * 10) / 10.0;
    }

    public StationTimeTableElement getStoppingTrainAfterCurrTime(MinimumStationInfoWithDateType key, String trnNo, String time) {
        return get(key).getStoppingTrainAfterCurrTime(trnNo, time);
    }

    public boolean isSameTrain(String trn1, String trn2, String lnCd, String dateType) {
        if (trn1.equals(trn2)) {
            return true;
        }
        if (lnCd.equals("2")) {
            MinimumStationInfo MSI = MinimumStationInfo.build("S1", "2", "211");
            MinimumStationInfoWithDateType key = new MinimumStationInfoWithDateType(MSI, dateType);
            SeongsuTimeTable seongsuTimeTable = (SeongsuTimeTable) map.get(key);
            HashMap<String, Integer> sameTrainMap = seongsuTimeTable.getSameTrainMap(dateType);

            return sameTrainMap.get(trn1).equals(sameTrainMap.get(trn2));
        }
        return false;
    }
}

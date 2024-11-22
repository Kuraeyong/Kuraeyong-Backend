package kuraeyong.backend.service;

import kuraeyong.backend.common.exception.DomainInitializationException;
import kuraeyong.backend.common.exception.ErrorMessage;
import kuraeyong.backend.common.response.BaseResponse;
import kuraeyong.backend.common.response.ResponseStatus;
import kuraeyong.backend.domain.constant.FileType;
import kuraeyong.backend.domain.station.info.MinimumStationInfo;
import kuraeyong.backend.manager.station.EdgeInfoManager;
import kuraeyong.backend.manager.station.StationCongestionManager;
import kuraeyong.backend.manager.station.StationConvenienceManager;
import kuraeyong.backend.manager.station.StationDBInitializer;
import kuraeyong.backend.manager.station.StationInfoManager;
import kuraeyong.backend.manager.station.StationTimeTableElementManager;
import kuraeyong.backend.manager.station.StationTrfWeightManager;
import kuraeyong.backend.util.FlatFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private final StationTimeTableElementManager stationTimeTableElementManager;

    private final static String BASE_URL = "src/main/resources/xlsx/";

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
     * 역사 시간표 API 응답 결과를 역사 시간표 파일에 저장
     *
     * @return 응답 결과 저장 성공 여부
     */
    public ResponseStatus saveStationTimeTableApiResult2Csv() {
        stationTimeTableElementManager.saveApiResult2Csv(stationInfoManager.findAll());
        return new BaseResponse<>();
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
}

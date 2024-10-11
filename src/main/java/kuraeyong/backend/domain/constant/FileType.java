package kuraeyong.backend.domain.constant;

public enum FileType {
    STATION_INFO("station_info.xlsx"),
    STATION_TRF_WEIGHT("station_trf_weight.xlsx"),
    STATION_CONGESTION("station_congestion.xlsx"),
    STATION_CONVENIENCE("station_convenience.xlsx"),
    EDGE_INFO("edge_info.xlsx");

    private final String fileType;

    private FileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileType;
    }
}

package kuraeyong.backend.domain.constant;

import lombok.Getter;

@Getter
public enum DomainType {
    STATION_TIME_TABLE("station_time_table"),
    STATION_CONGESTION("station_congestion");

    private final String domainName;

    DomainType(String domainName) {
        this.domainName = domainName;
    }
}

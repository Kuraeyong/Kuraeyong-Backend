package kuraeyong.backend.domain.constant;

public enum ConvenienceType {
    ELEVATOR("elevator"),
    DISABLED_TOILET("disabledToilet"),
    LACTATION_ROOM("lactationRoom"),
    WHEELCHAIR_CHARGER("wheelchairCharger"),
    WHEELCHAIR_LIFT("wheelchairLift"),
    MOBILE_SAFETY_BOARD("mobileSafetyBoard"),
    INFO_CENTER("infoCenter"),
    LOST_AND_FOUND_CENTER("lostAndFoundCenter"),
    AUTO_DISPENSER("autoDispenser");

    private final String convenienceType;

    ConvenienceType(String convenienceType) {
        this.convenienceType = convenienceType;
    }

    public String get() {
        return convenienceType;
    }
}

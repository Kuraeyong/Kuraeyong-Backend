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

    public static ConvenienceType parse(String convenienceName) {
        return switch (convenienceName) {
            case "elevator" -> ELEVATOR;
            case "disabledToilet" -> DISABLED_TOILET;
            case "lactationRoom" -> LACTATION_ROOM;
            case "wheelchairCharger" -> WHEELCHAIR_CHARGER;
            case "wheelchairLift" -> WHEELCHAIR_LIFT;
            case "mobileSafetyBoard" -> MOBILE_SAFETY_BOARD;
            case "infoCenter" -> INFO_CENTER;
            case "lostAndFoundCenter" -> LOST_AND_FOUND_CENTER;
            case "autoDispenser" -> AUTO_DISPENSER;
            default -> null;
        };
    }
}

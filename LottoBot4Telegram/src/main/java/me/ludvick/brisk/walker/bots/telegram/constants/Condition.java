package me.ludvick.brisk.walker.bots.telegram.constants;

public enum Condition {
    GREETING(1, "greeting"),
    ERROR(2, "error"),
    MAIN_MENU(3, "mainMenu"),
    SUB_MENU(4, "subMenu"),
    TOP_STRONG(5, "topStrong"),
    TOP_REGULAR(6, "topRegular"),
    LAST_STRONG(7, "lastStrong"),
    LAST_REGULAR(8, "lastRegular"),
    BUTTON_LAST_GAME(9, "lastGame"),
    BUTTON_LAST_MONTH(10, "lastMonth"),
    BUTTON_LAST_YEAR(11, "lastYear"),
    BUTTON_ALL_TIME(12, "allTime"),
    BUTTON_TOP(13, "top"),
    BUTTON_LAST(14, "last"),
    BUTTON_BACK(15, "back"),
    LAST_GAME_RESULT(16, "lastGameResult"),
    NUMBERS_RESULT(17, "gameResult");

    private final String code;

    Condition(int id, String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}
